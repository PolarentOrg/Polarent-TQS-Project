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
    getEquipmentDetails: (id) => fetch(`${API_BASE}/listings/${id}/details`).then(r => r.ok ? r.json() : null),
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

    // Bookings
    getBookingsByRenter: (renterId) => fetch(`${API_BASE}/bookings/renter/${renterId}`).then(r => r.ok ? r.json() : []),
    getBookingsByOwner: (ownerId) => fetch(`${API_BASE}/bookings/owner/${ownerId}`).then(r => r.ok ? r.json() : []),
    updateBookingStatus: (id, status) => fetch(`${API_BASE}/bookings/${id}/status?status=${status}`, { method: 'PATCH' }).then(r => r.json()),
    cancelBooking: (id) => fetch(`${API_BASE}/bookings/${id}/cancel`, { method: 'PATCH' }).then(r => r.json()),
    declineBooking: (id) => fetch(`${API_BASE}/bookings/${id}/decline`, { method: 'PATCH' }).then(r => r.json()),

    // Renter Dashboard
    getRenterDashboard: (renterId) =>
        fetch(`${API_BASE}/bookings/renter/${renterId}/dashboard`)
            .then(r => { if (!r.ok) throw r; return r.json(); }),

    getRenterRentals: (renterId, status) => {
        const url = status
            ? `${API_BASE}/bookings/renter/${renterId}/rentals?status=${status}`
            : `${API_BASE}/bookings/renter/${renterId}/rentals`;
        return fetch(url).then(r => { if (!r.ok) throw r; return r.json(); });
    },

    getRenterStats: (renterId) =>
        fetch(`${API_BASE}/bookings/renter/${renterId}/stats`)
            .then(r => { if (!r.ok) throw r; return r.json(); }),

    getRenterDetailedBookings: (renterId) =>
        fetch(`${API_BASE}/bookings/renter/${renterId}/detailed`)
            .then(r => { if (!r.ok) throw r; return r.json(); }),

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
    }).then(r => r.json()),

    // Admin - Listings Management
    removeInappropriateListing: (id) =>
        fetch(`${API_BASE}/listings/admin/${id}`, { method: 'DELETE' })
            .then(r => {
                if (!r.ok) throw new Error('Failed to remove listing');
                return r;
            }),

    getAllListingsForAdmin: () =>
        fetch(`${API_BASE}/listings/admin/all`)
            .then(r => {
                if (!r.ok) throw new Error('Failed to load listings for admin');
                return r.json();
            })
};

// Admin APIs - User Management
const adminApi = {
    getAllUsers: () => fetch(`${API_BASE}/users`).then(r => r.ok ? r.json() : []),
    getUserById: (id) => fetch(`${API_BASE}/users/${id}`).then(r => r.ok ? r.json() : null),
    activateUser: (id) => fetch(`${API_BASE}/users/${id}/activate`, { method: 'PATCH' }).then(r => r.json()),
    deactivateUser: (id) => fetch(`${API_BASE}/users/${id}/deactivate`, { method: 'PATCH' }).then(r => r.json()),
    deleteUser: (id) => fetch(`${API_BASE}/users/${id}`, { method: 'DELETE' })
};