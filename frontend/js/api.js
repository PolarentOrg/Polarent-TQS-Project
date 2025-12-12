const API_BASE = '/api';

const api = {
    // Auth
    login: (email, password) => fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    }).then(r => { if (!r.ok) throw r; return r.json(); }),
    
    register: (data) => fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(r => { if (!r.ok) throw r; return r.json(); }),

    // Listings
    getListings: () => fetch(`${API_BASE}/listings/enabled`).then(r => r.ok ? r.json() : []),
    getListingById: (id) => fetch(`${API_BASE}/listings/${id}`).then(r => r.ok ? r.json() : null),
    searchListings: (q) => fetch(`${API_BASE}/listings/search?q=${encodeURIComponent(q || '')}`).then(r => r.ok ? r.json() : []),
    getMyListings: (ownerId) => fetch(`${API_BASE}/listings/owner/${ownerId}`).then(r => r.ok ? r.json() : []),
    
    // Filters
    filterAdvanced: (params) => {
        const query = new URLSearchParams(Object.entries(params).filter(([_, v]) => v != null && v !== '')).toString();
        return fetch(`${API_BASE}/listings/filter/advanced?${query}`).then(r => r.ok ? r.json() : []);
    },
    getCities: () => fetch(`${API_BASE}/listings/cities`).then(r => r.ok ? r.json() : []),
    getDistricts: () => fetch(`${API_BASE}/listings/districts`).then(r => r.ok ? r.json() : []),

    createListing: (data) => fetch(`${API_BASE}/listings`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(r => r.json()),
    updateListing: (id, data) => fetch(`${API_BASE}/listings/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(r => r.json()),
    deleteListing: (userId, listingId) => fetch(`${API_BASE}/listings/${userId}/${listingId}`, { method: 'DELETE' }),

    // Requests
    createRequest: (data) => fetch(`${API_BASE}/requests`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(r => { if (!r.ok) throw r; return r.json(); }),
    getRequestsByListing: (listingId) => fetch(`${API_BASE}/requests/listing/${listingId}`).then(r => r.ok ? r.json() : []),
    getMyRequests: (requesterId) => fetch(`${API_BASE}/requests/requester/${requesterId}`).then(r => r.ok ? r.json() : []),
    cancelRequest: (requestId) => fetch(`${API_BASE}/requests/${requestId}`, { method: 'DELETE' }),
    acceptRequest: (requestData) => fetch(`${API_BASE}/requests/accept`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestData)
    }).then(r => r.json())
};
