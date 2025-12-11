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
    searchListings: (q) => fetch(`${API_BASE}/listings/search?q=${encodeURIComponent(q || '')}`).then(r => r.ok ? r.json() : []),
    getMyListings: (ownerId) => fetch(`${API_BASE}/listings/owner/${ownerId}`).then(r => r.ok ? r.json() : []),
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
    acceptRequest: (requestData) => fetch(`${API_BASE}/requests/accept`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestData)
    }).then(r => r.json())
};
