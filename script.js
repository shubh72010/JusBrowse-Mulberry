// ========== STATE MANAGEMENT ==========
const state = {
    tabs: [],
    activeTabId: null,
    history: [],
    bookmarks: [
        { id: 1, title: 'GitHub', url: 'https://github.com', icon: 'G' },
        { id: 2, title: 'Stack Overflow', url: 'https://stackoverflow.com', icon: 'S' },
        { id: 3, title: 'MDN Web Docs', url: 'https://developer.mozilla.org', icon: 'M' },
        { id: 4, title: 'CodePen', url: 'https://codepen.io', icon: 'C' }
    ],
    settings: {
        darkMode: true,
        searchEngine: 'duckduckgo',
        homePage: 'about:blank'
    },
    nextTabId: 1
};

// ========== DOM ELEMENTS ==========
const elements = {
    tabsContainer: document.getElementById('tabsContainer'),
    newTabBtn: document.getElementById('newTabBtn'),
    backBtn: document.getElementById('backBtn'),
    forwardBtn: document.getElementById('forwardBtn'),
    reloadBtn: document.getElementById('reloadBtn'),
    homeBtn: document.getElementById('homeBtn'),
    addressInput: document.getElementById('addressInput'),
    suggestionsDropdown: document.getElementById('suggestionsDropdown'),
    bookmarksBtn: document.getElementById('bookmarksBtn'),
    historyBtn: document.getElementById('historyBtn'),
    settingsBtn: document.getElementById('settingsBtn'),
    viewport: document.getElementById('viewport'),
    loadingBar: document.getElementById('loadingBar'),
    bookmarksPanel: document.getElementById('bookmarksPanel'),
    historyPanel: document.getElementById('historyPanel'),
    settingsPanel: document.getElementById('settingsPanel'),
    bookmarksList: document.getElementById('bookmarksList'),
    historyList: document.getElementById('historyList'),
    contextMenu: document.getElementById('contextMenu'),
    newTabPage: document.getElementById('newTabPage'),
    newTabSearch: document.getElementById('newTabSearch')
};

// ========== TAB MANAGEMENT ==========
class Tab {
    constructor(url = 'about:blank') {
        this.id = state.nextTabId++;
        this.url = url;
        this.title = 'New Tab';
        this.favicon = '🌐';
        this.history = [];
        this.historyIndex = -1;
        this.iframe = null;
        this.isNewTabPage = url === 'about:blank';
    }

    navigate(url) {
        if (!url || url === 'about:blank') {
            this.isNewTabPage = true;
            this.url = 'about:blank';
            this.title = 'New Tab';
            return;
        }

        this.isNewTabPage = false;
        this.url = normalizeURL(url);

        // Add to history
        if (this.historyIndex < this.history.length - 1) {
            this.history = this.history.slice(0, this.historyIndex + 1);
        }
        this.history.push(this.url);
        this.historyIndex++;

        // Add to global history
        addToHistory(this.url, this.title);

        // Update iframe
        if (this.iframe && !this.isNewTabPage) {
            showLoading();
            this.iframe.src = this.url;
        }
    }

    canGoBack() {
        return this.historyIndex > 0;
    }

    canGoForward() {
        return this.historyIndex < this.history.length - 1;
    }

    goBack() {
        if (this.canGoBack()) {
            this.historyIndex--;
            this.url = this.history[this.historyIndex];
            if (this.iframe) {
                this.iframe.src = this.url;
            }
        }
    }

    goForward() {
        if (this.canGoForward()) {
            this.historyIndex++;
            this.url = this.history[this.historyIndex];
            if (this.iframe) {
                this.iframe.src = this.url;
            }
        }
    }

    reload() {
        if (this.iframe && !this.isNewTabPage) {
            showLoading();
            this.iframe.src = this.iframe.src;
        }
    }
}

function createTab(url = 'about:blank') {
    const tab = new Tab(url);
    state.tabs.push(tab);

    // Create iframe
    if (!tab.isNewTabPage) {
        const iframe = document.createElement('iframe');
        iframe.className = 'browser-frame';
        iframe.id = `frame-${tab.id}`;
        iframe.sandbox = 'allow-same-origin allow-scripts allow-forms allow-popups';
        tab.iframe = iframe;
        elements.viewport.appendChild(iframe);

        iframe.addEventListener('load', () => {
            hideLoading();
            try {
                tab.title = iframe.contentDocument?.title || extractDomain(tab.url);
            } catch (e) {
                tab.title = extractDomain(tab.url);
            }
            updateTab(tab.id);
            updateAddressBar();
        });

        iframe.addEventListener('error', () => {
            hideLoading();
        });
    }

    // Create tab element
    const tabElement = document.createElement('div');
    tabElement.className = 'tab';
    tabElement.id = `tab-${tab.id}`;
    tabElement.innerHTML = `
        <span class="tab-favicon">${tab.favicon}</span>
        <span class="tab-title">${tab.title}</span>
        <button class="tab-close">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
        </button>
    `;

    tabElement.addEventListener('click', (e) => {
        if (!e.target.closest('.tab-close')) {
            switchTab(tab.id);
        }
    });

    tabElement.addEventListener('contextmenu', (e) => {
        e.preventDefault();
        showContextMenu(e, tab.id);
    });

    tabElement.querySelector('.tab-close').addEventListener('click', (e) => {
        e.stopPropagation();
        closeTab(tab.id);
    });

    elements.tabsContainer.appendChild(tabElement);
    switchTab(tab.id);

    if (url !== 'about:blank') {
        tab.navigate(url);
    }

    return tab;
}

function switchTab(tabId) {
    state.activeTabId = tabId;
    const activeTab = getActiveTab();

    // Update tab UI
    document.querySelectorAll('.tab').forEach(el => el.classList.remove('active'));
    document.getElementById(`tab-${tabId}`)?.classList.add('active');

    // Update viewport
    document.querySelectorAll('.browser-frame').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.new-tab-page').forEach(el => el.classList.remove('active'));

    if (activeTab.isNewTabPage) {
        elements.newTabPage.classList.add('active');
    } else if (activeTab.iframe) {
        activeTab.iframe.classList.add('active');
    }

    updateAddressBar();
    updateNavigationButtons();
}

function closeTab(tabId) {
    const tabIndex = state.tabs.findIndex(t => t.id === tabId);
    if (tabIndex === -1) return;

    const tab = state.tabs[tabIndex];

    // Remove iframe
    if (tab.iframe) {
        tab.iframe.remove();
    }

    // Remove tab element
    document.getElementById(`tab-${tabId}`)?.remove();

    // Remove from state
    state.tabs.splice(tabIndex, 1);

    // Switch to another tab or create new one
    if (state.tabs.length === 0) {
        createTab();
    } else if (state.activeTabId === tabId) {
        const newActiveIndex = Math.max(0, tabIndex - 1);
        switchTab(state.tabs[newActiveIndex].id);
    }
}

function updateTab(tabId) {
    const tab = state.tabs.find(t => t.id === tabId);
    if (!tab) return;

    const tabElement = document.getElementById(`tab-${tabId}`);
    if (tabElement) {
        tabElement.querySelector('.tab-title').textContent = tab.title;
        tabElement.querySelector('.tab-favicon').textContent = tab.favicon;
    }
}

function getActiveTab() {
    return state.tabs.find(t => t.id === state.activeTabId);
}

// ========== NAVIGATION ==========
function navigate(input) {
    const activeTab = getActiveTab();
    if (!activeTab) return;

    let url = input.trim();

    if (!url) return;

    // Check if it's a search query or URL
    if (isURL(url)) {
        activeTab.navigate(url);
    } else {
        // Perform search
        const searchURL = getSearchURL(url);
        activeTab.navigate(searchURL);
    }

    updateAddressBar();
    updateNavigationButtons();
    elements.suggestionsDropdown.classList.remove('visible');
}

function normalizeURL(url) {
    if (!url.match(/^https?:\/\//)) {
        return 'https://' + url;
    }
    return url;
}

function isURL(input) {
    // Check if input looks like a URL
    const urlPattern = /^([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(\/.*)?$/;
    const localhostPattern = /^localhost(:\d+)?(\/.*)?$/;
    const ipPattern = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(:\d+)?(\/.*)?$/;

    return urlPattern.test(input) || localhostPattern.test(input) || ipPattern.test(input) || input.startsWith('http://') || input.startsWith('https://');
}

function getSearchURL(query) {
    const encodedQuery = encodeURIComponent(query);
    const searchEngines = {
        duckduckgo: `https://duckduckgo.com/?q=${encodedQuery}`,
        google: `https://www.google.com/search?q=${encodedQuery}`,
        bing: `https://www.bing.com/search?q=${encodedQuery}`
    };
    return searchEngines[state.settings.searchEngine] || searchEngines.duckduckgo;
}

function extractDomain(url) {
    try {
        const urlObj = new URL(url);
        return urlObj.hostname;
    } catch {
        return url;
    }
}

function updateAddressBar() {
    const activeTab = getActiveTab();
    if (activeTab) {
        elements.addressInput.value = activeTab.isNewTabPage ? '' : activeTab.url;
    }
}

function updateNavigationButtons() {
    const activeTab = getActiveTab();
    if (activeTab) {
        elements.backBtn.disabled = !activeTab.canGoBack();
        elements.forwardBtn.disabled = !activeTab.canGoForward();
    }
}

// ========== BOOKMARKS ==========
function addBookmark(url, title) {
    const bookmark = {
        id: Date.now(),
        title: title || extractDomain(url),
        url: url,
        icon: title ? title[0].toUpperCase() : '🔖'
    };
    state.bookmarks.push(bookmark);
    renderBookmarks();
    saveToLocalStorage();
}

function removeBookmark(id) {
    state.bookmarks = state.bookmarks.filter(b => b.id !== id);
    renderBookmarks();
    saveToLocalStorage();
}

function isBookmarked(url) {
    return state.bookmarks.some(b => b.url === url);
}

function renderBookmarks(filter = '') {
    const filteredBookmarks = state.bookmarks.filter(b =>
        b.title.toLowerCase().includes(filter.toLowerCase()) ||
        b.url.toLowerCase().includes(filter.toLowerCase())
    );

    elements.bookmarksList.innerHTML = filteredBookmarks.map(bookmark => `
        <div class="bookmark-item" data-url="${bookmark.url}">
            <div class="bookmark-icon">${bookmark.icon}</div>
            <div class="bookmark-info">
                <div class="bookmark-title">${bookmark.title}</div>
                <div class="bookmark-url">${bookmark.url}</div>
            </div>
            <button class="bookmark-delete" data-id="${bookmark.id}">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="6" x2="6" y2="18"/>
                    <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
            </button>
        </div>
    `).join('');

    // Add event listeners
    elements.bookmarksList.querySelectorAll('.bookmark-item').forEach(item => {
        item.addEventListener('click', (e) => {
            if (!e.target.closest('.bookmark-delete')) {
                const url = item.dataset.url;
                navigate(url);
                closeAllPanels();
            }
        });
    });

    elements.bookmarksList.querySelectorAll('.bookmark-delete').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const id = parseInt(btn.dataset.id);
            removeBookmark(id);
        });
    });
}

// ========== HISTORY ==========
function addToHistory(url, title) {
    const historyItem = {
        id: Date.now(),
        url: url,
        title: title || extractDomain(url),
        timestamp: new Date()
    };
    state.history.unshift(historyItem);

    // Keep only last 100 items
    if (state.history.length > 100) {
        state.history = state.history.slice(0, 100);
    }

    saveToLocalStorage();
}

function clearHistory() {
    state.history = [];
    renderHistory();
    saveToLocalStorage();
}

function renderHistory(filter = '') {
    const filteredHistory = state.history.filter(h =>
        h.title.toLowerCase().includes(filter.toLowerCase()) ||
        h.url.toLowerCase().includes(filter.toLowerCase())
    );

    elements.historyList.innerHTML = filteredHistory.map(item => {
        const time = formatTime(item.timestamp);
        return `
            <div class="history-item" data-url="${item.url}">
                <div class="history-time">${time}</div>
                <div class="history-info">
                    <div class="history-title">${item.title}</div>
                    <div class="history-url">${item.url}</div>
                </div>
            </div>
        `;
    }).join('');

    elements.historyList.querySelectorAll('.history-item').forEach(item => {
        item.addEventListener('click', () => {
            const url = item.dataset.url;
            navigate(url);
            closeAllPanels();
        });
    });
}

function formatTime(timestamp) {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now - date;
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `${days}d ago`;
    if (hours > 0) return `${hours}h ago`;
    if (minutes > 0) return `${minutes}m ago`;
    return 'Just now';
}

// ========== PANELS ==========
function togglePanel(panel) {
    closeAllPanels();
    panel.classList.add('open');
}

function closeAllPanels() {
    document.querySelectorAll('.side-panel').forEach(panel => {
        panel.classList.remove('open');
    });
    document.querySelectorAll('.action-btn').forEach(btn => {
        btn.classList.remove('active');
    });
}

// ========== LOADING ==========
function showLoading() {
    elements.loadingBar.classList.add('loading');
    elements.reloadBtn.classList.add('loading');
}

function hideLoading() {
    setTimeout(() => {
        elements.loadingBar.classList.remove('loading');
        elements.reloadBtn.classList.remove('loading');
    }, 300);
}

// ========== CONTEXT MENU ==========
function showContextMenu(e, tabId) {
    elements.contextMenu.style.left = e.pageX + 'px';
    elements.contextMenu.style.top = e.pageY + 'px';
    elements.contextMenu.classList.add('visible');
    elements.contextMenu.dataset.tabId = tabId;
}

function hideContextMenu() {
    elements.contextMenu.classList.remove('visible');
}

// ========== SUGGESTIONS ==========
function showSuggestions(query) {
    if (!query) {
        elements.suggestionsDropdown.classList.remove('visible');
        return;
    }

    const suggestions = [];

    // Search bookmarks
    state.bookmarks.forEach(bookmark => {
        if (bookmark.title.toLowerCase().includes(query.toLowerCase()) ||
            bookmark.url.toLowerCase().includes(query.toLowerCase())) {
            suggestions.push({
                type: 'bookmark',
                title: bookmark.title,
                url: bookmark.url
            });
        }
    });

    // Search history
    state.history.forEach(item => {
        if (suggestions.length >= 5) return;
        if (item.title.toLowerCase().includes(query.toLowerCase()) ||
            item.url.toLowerCase().includes(query.toLowerCase())) {
            suggestions.push({
                type: 'history',
                title: item.title,
                url: item.url
            });
        }
    });

    if (suggestions.length > 0) {
        elements.suggestionsDropdown.innerHTML = suggestions.slice(0, 5).map(s => {
            const highlightedTitle = s.title.replace(
                new RegExp(query, 'gi'),
                match => `<strong>${match}</strong>`
            );
            return `
                <div class="suggestion-item" data-url="${s.url}">
                    ${highlightedTitle}
                </div>
            `;
        }).join('');

        elements.suggestionsDropdown.querySelectorAll('.suggestion-item').forEach(item => {
            item.addEventListener('click', () => {
                navigate(item.dataset.url);
            });
        });

        elements.suggestionsDropdown.classList.add('visible');
    } else {
        elements.suggestionsDropdown.classList.remove('visible');
    }
}

// ========== STORAGE ==========
function saveToLocalStorage() {
    try {
        localStorage.setItem('browserState', JSON.stringify({
            bookmarks: state.bookmarks,
            history: state.history,
            settings: state.settings
        }));
    } catch (e) {
        console.error('Failed to save to localStorage', e);
    }
}

function loadFromLocalStorage() {
    try {
        const saved = localStorage.getItem('browserState');
        if (saved) {
            const data = JSON.parse(saved);
            state.bookmarks = data.bookmarks || state.bookmarks;
            state.history = data.history || [];
            state.settings = { ...state.settings, ...data.settings };
        }
    } catch (e) {
        console.error('Failed to load from localStorage', e);
    }
}

// ========== EVENT LISTENERS ==========
function initEventListeners() {
    // New Tab
    elements.newTabBtn.addEventListener('click', () => createTab());

    // Navigation
    elements.backBtn.addEventListener('click', () => {
        const activeTab = getActiveTab();
        if (activeTab) activeTab.goBack();
    });

    elements.forwardBtn.addEventListener('click', () => {
        const activeTab = getActiveTab();
        if (activeTab) activeTab.goForward();
    });

    elements.reloadBtn.addEventListener('click', () => {
        const activeTab = getActiveTab();
        if (activeTab) activeTab.reload();
    });

    elements.homeBtn.addEventListener('click', () => {
        navigate(state.settings.homePage || 'about:blank');
    });

    // Address Bar
    elements.addressInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            navigate(elements.addressInput.value);
        }
    });

    elements.addressInput.addEventListener('input', (e) => {
        showSuggestions(e.target.value);
    });

    elements.addressInput.addEventListener('focus', () => {
        elements.addressInput.select();
    });

    // New Tab Search
    elements.newTabSearch.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            navigate(elements.newTabSearch.value);
        }
    });

    // Startpage Search Button
    document.getElementById('startpageSearchBtn')?.addEventListener('click', () => {
        navigate(elements.newTabSearch.value);
    });

    // Startpage Action Buttons
    document.getElementById('startpageBookmarksBtn')?.addEventListener('click', () => {
        elements.bookmarksBtn.click();
    });

    document.getElementById('startpageSettingsBtn')?.addEventListener('click', () => {
        elements.settingsBtn.click();
    });

    // Panels
    elements.bookmarksBtn.addEventListener('click', () => {
        if (elements.bookmarksPanel.classList.contains('open')) {
            closeAllPanels();
        } else {
            togglePanel(elements.bookmarksPanel);
            elements.bookmarksBtn.classList.add('active');
            renderBookmarks();
        }
    });

    elements.historyBtn.addEventListener('click', () => {
        if (elements.historyPanel.classList.contains('open')) {
            closeAllPanels();
        } else {
            togglePanel(elements.historyPanel);
            elements.historyBtn.classList.add('active');
            renderHistory();
        }
    });

    elements.settingsBtn.addEventListener('click', () => {
        if (elements.settingsPanel.classList.contains('open')) {
            closeAllPanels();
        } else {
            togglePanel(elements.settingsPanel);
            elements.settingsBtn.classList.add('active');
        }
    });

    // Close Panel Buttons
    document.getElementById('closeBookmarksBtn').addEventListener('click', closeAllPanels);
    document.getElementById('closeHistoryBtn').addEventListener('click', closeAllPanels);
    document.getElementById('closeSettingsBtn').addEventListener('click', closeAllPanels);

    // Search filters
    document.getElementById('bookmarkSearch').addEventListener('input', (e) => {
        renderBookmarks(e.target.value);
    });

    document.getElementById('historySearch').addEventListener('input', (e) => {
        renderHistory(e.target.value);
    });

    document.getElementById('clearHistoryBtn').addEventListener('click', () => {
        if (confirm('Clear all browsing history?')) {
            clearHistory();
        }
    });

    // Settings
    document.getElementById('darkModeToggle').addEventListener('change', (e) => {
        state.settings.darkMode = e.target.checked;
        saveToLocalStorage();
    });

    document.getElementById('searchEngineSelect').addEventListener('change', (e) => {
        state.settings.searchEngine = e.target.value;
        saveToLocalStorage();
    });

    document.getElementById('homePageInput').addEventListener('change', (e) => {
        state.settings.homePage = e.target.value;
        saveToLocalStorage();
    });

    // Context Menu
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.context-menu')) {
            hideContextMenu();
        }
        if (!e.target.closest('.address-bar')) {
            elements.suggestionsDropdown.classList.remove('visible');
        }
    });

    elements.contextMenu.addEventListener('click', (e) => {
        const action = e.target.dataset.action;
        const tabId = parseInt(elements.contextMenu.dataset.tabId);

        if (action === 'reload') {
            const tab = state.tabs.find(t => t.id === tabId);
            if (tab) tab.reload();
        } else if (action === 'bookmark') {
            const tab = state.tabs.find(t => t.id === tabId);
            if (tab && !tab.isNewTabPage) {
                addBookmark(tab.url, tab.title);
            }
        } else if (action === 'closeTab') {
            closeTab(tabId);
        } else if (action === 'closeOtherTabs') {
            state.tabs.filter(t => t.id !== tabId).forEach(t => closeTab(t.id));
        }

        hideContextMenu();
    });

    // Keyboard Shortcuts
    document.addEventListener('keydown', (e) => {
        // Ctrl+T - New Tab
        if (e.ctrlKey && e.key === 't') {
            e.preventDefault();
            createTab();
        }
        // Ctrl+W - Close Tab
        else if (e.ctrlKey && e.key === 'w') {
            e.preventDefault();
            const activeTab = getActiveTab();
            if (activeTab) closeTab(activeTab.id);
        }
        // Ctrl+R - Reload
        else if (e.ctrlKey && e.key === 'r') {
            e.preventDefault();
            const activeTab = getActiveTab();
            if (activeTab) activeTab.reload();
        }
        // Ctrl+D - Bookmark
        else if (e.ctrlKey && e.key === 'd') {
            e.preventDefault();
            const activeTab = getActiveTab();
            if (activeTab && !activeTab.isNewTabPage && !isBookmarked(activeTab.url)) {
                addBookmark(activeTab.url, activeTab.title);
            }
        }
        // Ctrl+H - History
        else if (e.ctrlKey && e.key === 'h') {
            e.preventDefault();
            elements.historyBtn.click();
        }
        // Alt+Left - Back
        else if (e.altKey && e.key === 'ArrowLeft') {
            e.preventDefault();
            const activeTab = getActiveTab();
            if (activeTab) activeTab.goBack();
        }
        // Alt+Right - Forward
        else if (e.altKey && e.key === 'ArrowRight') {
            e.preventDefault();
            const activeTab = getActiveTab();
            if (activeTab) activeTab.goForward();
        }
        // Ctrl+L - Focus Address Bar
        else if (e.ctrlKey && e.key === 'l') {
            e.preventDefault();
            elements.addressInput.focus();
            elements.addressInput.select();
        }
    });
}

// ========== INITIALIZATION ==========
function init() {
    loadFromLocalStorage();
    initEventListeners();

    // Load settings
    document.getElementById('darkModeToggle').checked = state.settings.darkMode;
    document.getElementById('searchEngineSelect').value = state.settings.searchEngine;
    document.getElementById('homePageInput').value = state.settings.homePage;

    // Create initial tab
    createTab();
}

// Start the browser
init();
