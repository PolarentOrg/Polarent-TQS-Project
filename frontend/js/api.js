const API_BASE = '/api';

const api = {
    // Listings
    getListings: () => fetch(`${API_BASE}/listings`).then(r => r.json()),
    createListing: (data) => fetch(`${API_BASE}/listings`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(r => r.json()),
    deleteListing: (userId, listingId) => fetch(`${API_BASE}/listings/${userId}/${listingId}`, { method: 'DELETE' }),

    // Bookings
    getBookings: () => fetch(`${API_BASE}/bookings`).then(r => r.json()),
    getBookingsByRenter: (renterId) => fetch(`${API_BASE}/bookings/renter/${renterId}`).then(r => r.json()),
    getBooking: (id) => fetch(`${API_BASE}/bookings/${id}`).then(r => r.json()),
    createBooking: (data) => fetch(`${API_BASE}/bookings`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(r => r.json()),
    updateBookingStatus: (id, status) => fetch(`${API_BASE}/bookings/${id}/status?status=${status}`, { method: 'PATCH' }).then(r => r.json()),
    cancelBooking: (id) => fetch(`${API_BASE}/bookings/${id}/cancel`, { method: 'PATCH' }).then(r => r.json()),
    deleteBooking: (id) => fetch(`${API_BASE}/bookings/${id}`, { method: 'DELETE' }),

    // Requests
    getRequestsByListing: (listingId) => fetch(`${API_BASE}/requests/listing/${listingId}`).then(r => r.json()),
    acceptRequest: (requestData) => fetch(`${API_BASE}/requests/accept`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestData)
    }).then(r => r.json())
};
