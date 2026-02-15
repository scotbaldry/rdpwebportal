/* Shared API utilities - pages include inline JS but this is
   available for any future modularization */
var Api = (function() {
    function getCsrfToken() {
        var match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
        return match ? decodeURIComponent(match[1]) : null;
    }

    function apiFetch(url, opts) {
        opts = opts || {};
        var headers = opts.headers || {};
        var csrf = getCsrfToken();
        if (csrf) headers['X-XSRF-TOKEN'] = csrf;
        if (opts.body && typeof opts.body === 'object') {
            headers['Content-Type'] = 'application/json';
            opts.body = JSON.stringify(opts.body);
        }
        opts.headers = headers;
        opts.credentials = 'same-origin';
        return fetch(url, opts);
    }

    return { fetch: apiFetch, csrf: getCsrfToken };
})();
