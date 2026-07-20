"use strict";

(function () {
    if (window.__jusbrowseWebAuthn) return;
    window.__jusbrowseWebAuthn = true;

    if (!navigator.credentials || !navigator.credentials.create || !navigator.credentials.get) return;

    var origCreate = navigator.credentials.create;
    var origGet = navigator.credentials.get;
    var requestIdCounter = 0;

    function arrayBufferToBase64url(buffer) {
        var bytes = new Uint8Array(buffer);
        var binary = '';
        for (var i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
    }

    function base64urlToArrayBuffer(base64url) {
        var padding = '='.repeat((4 - base64url.length % 4) % 4);
        var base64 = base64url.replace(/-/g, '+').replace(/_/g, '/') + padding;
        var binary = atob(base64);
        var buffer = new ArrayBuffer(binary.length);
        var view = new Uint8Array(buffer);
        for (var i = 0; i < binary.length; i++) {
            view[i] = binary.charCodeAt(i);
        }
        return buffer;
    }

    function serializeCreateOptions(publicKey) {
        var clone = {};
        for (var key in publicKey) {
            if (!publicKey.hasOwnProperty(key)) continue;
            if (key === 'challenge') {
                clone.challenge = arrayBufferToBase64url(publicKey.challenge);
            } else if (key === 'user') {
                clone.user = {
                    id: arrayBufferToBase64url(publicKey.user.id),
                    name: publicKey.user.name,
                    displayName: publicKey.user.displayName
                };
                if (publicKey.user.icon) clone.user.icon = publicKey.user.icon;
            } else if (key === 'excludeCredentials' && publicKey.excludeCredentials) {
                clone.excludeCredentials = publicKey.excludeCredentials.map(function (cred) {
                    var c = { type: cred.type, id: arrayBufferToBase64url(cred.id) };
                    if (cred.transports) c.transports = cred.transports;
                    return c;
                });
            } else {
                clone[key] = publicKey[key];
            }
        }
        return clone;
    }

    function serializeGetOptions(publicKey) {
        var clone = {};
        for (var key in publicKey) {
            if (!publicKey.hasOwnProperty(key)) continue;
            if (key === 'challenge') {
                clone.challenge = arrayBufferToBase64url(publicKey.challenge);
            } else if (key === 'allowCredentials' && publicKey.allowCredentials) {
                clone.allowCredentials = publicKey.allowCredentials.map(function (cred) {
                    var c = { type: cred.type, id: arrayBufferToBase64url(cred.id) };
                    if (cred.transports) c.transports = cred.transports;
                    return c;
                });
            } else {
                clone[key] = publicKey[key];
            }
        }
        return clone;
    }

    async function computeClientDataHash(type, challengeRaw, origin) {
        var challengeB64 = arrayBufferToBase64url(challengeRaw);
        var clientDataObj = {
            type: type,
            challenge: challengeB64,
            origin: origin,
            crossOrigin: false
        };
        var clientDataStr = JSON.stringify(clientDataObj);
        var encoder = new TextEncoder();
        var clientDataBytes = encoder.encode(clientDataStr);
        var hashBuffer = await crypto.subtle.digest('SHA-256', clientDataBytes);
        return {
            clientDataHash: arrayBufferToBase64url(hashBuffer),
            clientDataJSON: arrayBufferToBase64url(clientDataBytes.buffer)
        };
    }

    navigator.credentials.create = async function (options) {
        if (!options || !options.publicKey) {
            return origCreate.call(this, options);
        }
        var publicKey = options.publicKey;
        var origin = window.location.origin;
        var hashResult = await computeClientDataHash('webauthn.create', publicKey.challenge, origin);

        return new Promise(function (resolve, reject) {
            var requestId = 'wa_' + (++requestIdCounter) + '_' + Date.now();
            var timedOut = false;

            var handler = function (event) {
                if (event.data && event.data.type === 'webauthn_result' && event.data.requestId === requestId) {
                    window.removeEventListener('message', handler);
                    if (timedOut) return;
                    if (event.data.error) {
                        reject(new DOMException(event.data.error, event.data.errorType || 'UnknownError'));
                    } else {
                        var result = event.data.result;
                        if (result) {
                            result.clientDataJSON = hashResult.clientDataJSON;
                        }
                        resolve(buildCredential(result, true));
                    }
                }
            };
            window.addEventListener('message', handler);

            window.postMessage({
                type: 'webauthn_request',
                subType: 'create',
                requestId: requestId,
                publicKey: serializeCreateOptions(publicKey),
                clientDataHash: hashResult.clientDataHash
            }, '*');

            setTimeout(function () {
                timedOut = true;
                window.removeEventListener('message', handler);
                reject(new DOMException('The operation either timed out or was not allowed.', 'AbortError'));
            }, 120000);
        });
    };

    navigator.credentials.get = async function (options) {
        if (!options || !options.publicKey) {
            return origGet.call(this, options);
        }
        var publicKey = options.publicKey;
        var origin = window.location.origin;
        var hashResult = await computeClientDataHash('webauthn.get', publicKey.challenge, origin);

        return new Promise(function (resolve, reject) {
            var requestId = 'wa_' + (++requestIdCounter) + '_' + Date.now();
            var timedOut = false;

            var handler = function (event) {
                if (event.data && event.data.type === 'webauthn_result' && event.data.requestId === requestId) {
                    window.removeEventListener('message', handler);
                    if (timedOut) return;
                    if (event.data.error) {
                        reject(new DOMException(event.data.error, event.data.errorType || 'UnknownError'));
                    } else {
                        var result = event.data.result;
                        if (result) {
                            result.clientDataJSON = hashResult.clientDataJSON;
                        }
                        resolve(buildCredential(result, false));
                    }
                }
            };
            window.addEventListener('message', handler);

            window.postMessage({
                type: 'webauthn_request',
                subType: 'get',
                requestId: requestId,
                publicKey: serializeGetOptions(publicKey),
                clientDataHash: hashResult.clientDataHash
            }, '*');

            setTimeout(function () {
                timedOut = true;
                window.removeEventListener('message', handler);
                reject(new DOMException('The operation either timed out or was not allowed.', 'AbortError'));
            }, 120000);
        });
    };

    function buildCredential(result, isCreation) {
        var response;
        if (isCreation) {
            response = {
                clientDataJSON: base64urlToArrayBuffer(result.clientDataJSON),
                attestationObject: base64urlToArrayBuffer(result.attestationObject),
                getTransports: function () { return result.transports || []; },
                getAuthenticatorData: function () {
                    return result.authenticatorData
                        ? base64urlToArrayBuffer(result.authenticatorData)
                        : new ArrayBuffer(0);
                }
            };
        } else {
            response = {
                clientDataJSON: base64urlToArrayBuffer(result.clientDataJSON),
                authenticatorData: base64urlToArrayBuffer(result.authenticatorData),
                signature: base64urlToArrayBuffer(result.signature),
                userHandle: result.userHandle ? base64urlToArrayBuffer(result.userHandle) : null
            };
        }
        return {
            id: result.id,
            rawId: base64urlToArrayBuffer(result.rawId),
            type: 'public-key',
            response: response,
            getClientExtensionResults: function () { return {}; }
        };
    }
})();
