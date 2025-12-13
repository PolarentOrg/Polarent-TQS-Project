// State
let currentUser = null;
let allListings = [];

// DOM Elements
const modal = document.getElementById('modal');
const modalBody = document.getElementById('modal-body');
const closeBtn = document.querySelector('.close');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    setupAuth();
    setupNavigation();
    setupModal();
    checkSession();
});

// Auth
function setupAuth() {
    document.querySelectorAll('.auth-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            document.getElementById('login-form').style.display = tab.dataset.tab === 'login' ? 'block' : 'none';
            document.getElementById('register-form').style.display = tab.dataset.tab === 'register' ? 'block' : 'none';
        });
    });

    document.getElementById('login-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const user = await api.login(document.getElementById('login-email').value, document.getElementById('login-password').value);
            loginSuccess(user);
        } catch (err) {
            showToast('Invalid email or password', 'error');
        }
    });

    document.getElementById('register-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const user = await api.register({
                firstName: document.getElementById('register-firstname').value,
                lastName: document.getElementById('register-lastname').value,
                email: document.getElementById('register-email').value,
                password: document.getElementById('register-password').value
            });
            loginSuccess(user);
        } catch (err) {
            showToast('Registration failed. Email may already exist.', 'error');
        }
    });

    document.getElementById('logout-btn').addEventListener('click', logout);
}

function loginSuccess(user) {
    currentUser = user;
    sessionStorage.setItem('user', JSON.stringify(user));
    showApp();
    showToast(`Welcome, ${user.firstName}!`);
}

function logout() {
    currentUser = null;
    sessionStorage.removeItem('user');
    showAuth();
}

function checkSession() {
    const saved = sessionStorage.getItem('user');
    if (saved) {
        currentUser = JSON.parse(saved);
        showApp();
    } else {
        showAuth();
    }
}

function showAuth() {
    document.getElementById('auth-page').classList.add('active');
    document.getElementById('listings-page').classList.remove('active');
    document.getElementById('nav-links').style.display = 'none';
    document.getElementById('user-info').style.display = 'none';
}

function showApp() {
    document.getElementById('auth-page').classList.remove('active');
    document.getElementById('nav-links').style.display = 'flex';
    document.getElementById('user-info').style.display = 'flex';
    document.getElementById('user-display').textContent = `${currentUser.firstName} (${currentUser.role})`;
    showPage('listings');
}

// Navigation
function setupNavigation() {
    document.querySelectorAll('#nav-links a').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            showPage(e.target.dataset.page);
            document.querySelectorAll('#nav-links a').forEach(l => l.classList.remove('active'));
            e.target.classList.add('active');
        });
    });
    document.getElementById('create-listing-btn').addEventListener('click', showCreateListingModal);
}

function showPage(page) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById(`${page}-page`).classList.add('active');
    if (page === 'listings') loadListings();
    else if (page === 'dashboard') loadDashboard();
    else if (page === 'renter-dashboard') loadRenterDashboard();
    else if (page === 'my-listings') loadMyListings();
    else if (page === 'my-requests') loadMyRequests();
    else if (page === 'bookings') loadBookings();
    else if (page === 'requests') loadRequestsPage();
}

// Modal
function setupModal() {
    closeBtn.addEventListener('click', closeModal);
    modal.addEventListener('click', (e) => { if (e.target === modal) closeModal(); });
}

function openModal(content) {
    modalBody.innerHTML = content;
    modal.classList.remove('hidden');
}

function closeModal() {
    modal.classList.add('hidden');
}

// Toast
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    document.getElementById('toast-container').appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// Listings
async function loadListings() {
    try {
        allListings = await api.getListings();
        renderListings(allListings, 'listings-container', false);
        await loadFilterOptions();
        setupSearchAndFilters();
    } catch (e) {
        showToast('Failed to load listings', 'error');
    }
}

async function loadFilterOptions() {
    const [cities, districts] = await Promise.all([api.getCities(), api.getDistricts()]);
    document.getElementById('filter-city').innerHTML = '<option value="">All Cities</option>' + cities.map(c => `<option value="${c}">${c}</option>`).join('');
    document.getElementById('filter-district').innerHTML = '<option value="">All Districts</option>' + districts.map(d => `<option value="${d}">${d}</option>`).join('');
}

function setupSearchAndFilters() {
    const searchInput = document.getElementById('search-input');
    document.getElementById('search-btn').onclick = performSearch;
    searchInput.onkeypress = (e) => { if (e.key === 'Enter') performSearch(); };
    document.getElementById('filter-btn').onclick = applyFilters;
    document.getElementById('clear-btn').onclick = clearAll;
}

async function performSearch() {
    const q = document.getElementById('search-input').value.trim();
    try {
        const results = await api.searchListings(q);
        renderListings(results, 'listings-container', false);
    } catch (e) {
        showToast('Search failed', 'error');
    }
}

async function applyFilters() {
    const params = {
        min: document.getElementById('filter-min').value || null,
        max: document.getElementById('filter-max').value || null,
        city: document.getElementById('filter-city').value || null,
        district: document.getElementById('filter-district').value || null
    };
    try {
        const filtered = await api.filterAdvanced(params);
        renderListings(filtered, 'listings-container', false);
    } catch (e) {
        showToast('Filter failed', 'error');
    }
}

async function clearAll() {
    document.getElementById('search-input').value = '';
    document.getElementById('filter-min').value = '';
    document.getElementById('filter-max').value = '';
    document.getElementById('filter-city').value = '';
    document.getElementById('filter-district').value = '';
    await loadListings();
}

async function loadDashboard() {
    try {
        const [listings, bookings, requests] = await Promise.all([
            api.getMyListings(currentUser.userId),
            api.getBookingsByOwner(currentUser.userId),
            api.getRequestsByListing() // We'll need to aggregate this
        ]);

        // Update stats
        document.getElementById('total-listings').textContent = listings.length;
        document.getElementById('active-bookings').textContent = bookings.filter(b => b.status === 'PAID' || b.status === 'ACCEPTED').length;
        
        const totalRevenue = bookings.filter(b => b.status === 'PAID' || b.status === 'COMPLETED').reduce((sum, b) => sum + b.price, 0);
        document.getElementById('total-revenue').textContent = `€${totalRevenue.toFixed(2)}`;

        // Recent bookings
        const recentBookings = bookings.slice(0, 5);
        renderDashboardBookings(recentBookings);

    } catch (e) {
        showToast('Failed to load dashboard', 'error');
    }
}

function renderDashboardBookings(bookings) {
    const container = document.getElementById('recent-bookings');
    if (!bookings.length) {
        container.innerHTML = '<p class="empty">No recent bookings</p>';
        return;
    }
    container.innerHTML = bookings.map(b => `
        <div class="list-item">
            <div class="list-item-info">
                <strong>Booking #${b.id}</strong>
                <span class="badge badge-${getStatusClass(b.status)}">${b.status}</span>
            </div>
            <div class="list-item-details">
                <span>€${b.price.toFixed(2)}</span>
                <span>${new Date(b.createdAt).toLocaleDateString()}</span>
            </div>
        </div>
    `).join('');
}

async function loadMyListings() {
    try {
        const myListings = await api.getMyListings(currentUser.userId);
        renderListings(myListings, 'my-listings-container', true);
    } catch (e) {
        showToast('Failed to load listings', 'error');
    }
}

function renderListings(listings, containerId, isOwner) {
    const container = document.getElementById(containerId);
    if (!listings.length) {
        container.innerHTML = '<p class="empty">No listings found</p>';
        return;
    }
    container.innerHTML = listings.map(l => `
        <div class="card">
            <div class="card-header">
                <h3>${escapeHtml(l.title)}</h3>
                <span class="badge ${l.enabled ? 'badge-success' : 'badge-warning'}">${l.enabled ? 'Available' : 'Unavailable'}</span>
            </div>
            ${l.city || l.district ? `<p class="location">📍 ${escapeHtml([l.city, l.district].filter(Boolean).join(', '))}</p>` : ''}
            <div class="card-footer">
                <span class="price">€${l.dailyRate.toFixed(2)}/day</span>
                ${isOwner ? `
                    <button class="btn btn-secondary btn-sm" onclick="showEditListingModal(${l.id})">Edit</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteListing(${l.id})">Delete</button>
                ` : `
                    <button class="btn btn-info btn-sm" onclick="showListingDetails(${l.id})">Details</button>
                    <button class="btn btn-primary btn-sm" onclick="showRentModal(${l.id})">Rent</button>
                `}
            </div>
        </div>
    `).join('');
}

async function showListingDetails(listingId) {
    try {
        const listing = await api.getListingById(listingId);
        if (!listing) {
            showToast('Listing not found', 'error');
            return;
        }
        openModal(`
            <h3>${escapeHtml(listing.title)}</h3>
            <div class="listing-details">
                <p><strong>Description:</strong> ${escapeHtml(listing.description || 'No description available')}</p>
                <p><strong>Daily Rate:</strong> €${listing.dailyRate.toFixed(2)}/day</p>
                ${listing.city || listing.district ? `<p><strong>Location:</strong> ${escapeHtml([listing.city, listing.district].filter(Boolean).join(', '))}</p>` : ''}
                <p><strong>Status:</strong> <span class="badge ${listing.enabled ? 'badge-success' : 'badge-warning'}">${listing.enabled ? 'Available' : 'Unavailable'}</span></p>
                <p><strong>Listed:</strong> ${new Date(listing.createdAt).toLocaleDateString()}</p>
            </div>
            <div class="modal-actions">
                ${listing.enabled ? `<button class="btn btn-primary" onclick="closeModal(); showRentModal(${listing.id})">Rent This Item</button>` : ''}
                <button class="btn btn-secondary" onclick="closeModal()">Close</button>
            </div>
        `);
    } catch (e) {
        showToast('Failed to load listing details', 'error');
    }
}

function showCreateListingModal() {
    openModal(`
        <h3>Create New Listing</h3>
        <form id="create-listing-form">
            <div class="form-group"><label>Title</label><input type="text" name="title" required></div>
            <div class="form-group"><label>Description</label><textarea name="description" rows="3"></textarea></div>
            <div class="form-group"><label>Daily Rate (€)</label><input type="number" name="dailyRate" step="0.01" min="0.01" required></div>
            <div class="form-group"><label>City</label><input type="text" name="city"></div>
            <div class="form-group"><label>District</label><input type="text" name="district"></div>
            <div class="form-group"><label><input type="checkbox" name="enabled" checked> Available for rent</label></div>
            <button type="submit" class="btn btn-primary">Create</button>
        </form>
    `);
    document.getElementById('create-listing-form').addEventListener('submit', createListing);
}

async function createListing(e) {
    e.preventDefault();
    const form = e.target;
    try {
        await api.createListing({
            ownerId: currentUser.userId,
            title: form.title.value,
            description: form.description.value,
            dailyRate: parseFloat(form.dailyRate.value),
            city: form.city.value || null,
            district: form.district.value || null,
            enabled: form.enabled.checked
        });
        showToast('Listing created!');
        closeModal();
        loadMyListings();
    } catch (e) {
        showToast('Failed to create listing', 'error');
    }
}

async function deleteListing(listingId) {
    if (!confirm('Delete this listing?')) return;
    try {
        await api.deleteListing(currentUser.userId, listingId);
        showToast('Listing deleted');
        loadMyListings();
    } catch (e) {
        showToast('Failed to delete listing', 'error');
    }
}

function showRentModal(listingId) {
    const listing = allListings.find(l => l.id === listingId);
    openModal(`
        <h3>Rent: ${escapeHtml(listing.title)}</h3>
        <p class="price">€${listing.dailyRate.toFixed(2)}/day</p>
        <form id="rent-form">
            <input type="hidden" name="listingId" value="${listingId}">
            <div class="form-group"><label>Start Day (day of year)</label><input type="number" name="initialDate" min="1" max="365" required></div>
            <div class="form-group"><label>Duration (days)</label><input type="number" name="duration" min="1" required></div>
            <div class="form-group"><label>Note (optional)</label><textarea name="note" rows="2"></textarea></div>
            <p class="total">Total: €<span id="rent-total">0.00</span></p>
            <button type="submit" class="btn btn-primary">Submit Request</button>
        </form>
    `);
    const form = document.getElementById('rent-form');
    form.duration.addEventListener('input', () => {
        document.getElementById('rent-total').textContent = ((parseInt(form.duration.value) || 0) * listing.dailyRate).toFixed(2);
    });
    form.addEventListener('submit', (e) => submitRentRequest(e, listing));
}

async function submitRentRequest(e, listing) {
    e.preventDefault();
    const form = e.target;
    try {
        await api.createRequest({
            listingId: listing.id,
            requesterId: currentUser.userId,
            initialDate: parseInt(form.initialDate.value),
            duration: parseInt(form.duration.value),
            note: form.note.value || null
        });
        showToast('Request submitted!');
        closeModal();
    } catch (err) {
        showToast('Failed to submit request', 'error');
    }
}

async function showEditListingModal(listingId) {
    const listings = await api.getMyListings(currentUser.userId);
    const listing = listings.find(l => l.id === listingId);
    if (!listing) return;
    openModal(`
        <h3>Edit Listing</h3>
        <form id="edit-listing-form">
            <div class="form-group"><label>Title</label><input type="text" name="title" value="${escapeHtml(listing.title)}" required></div>
            <div class="form-group"><label>Description</label><textarea name="description" rows="3">${escapeHtml(listing.description || '')}</textarea></div>
            <div class="form-group"><label>Daily Rate (€)</label><input type="number" name="dailyRate" step="0.01" min="0.01" value="${listing.dailyRate}" required></div>
            <div class="form-group"><label>City</label><input type="text" name="city" value="${escapeHtml(listing.city || '')}"></div>
            <div class="form-group"><label>District</label><input type="text" name="district" value="${escapeHtml(listing.district || '')}"></div>
            <div class="form-group"><label><input type="checkbox" name="enabled" ${listing.enabled ? 'checked' : ''}> Available for rent</label></div>
            <button type="submit" class="btn btn-primary">Save</button>
        </form>
    `);
    document.getElementById('edit-listing-form').addEventListener('submit', (e) => updateListing(e, listingId));
}

async function updateListing(e, listingId) {
    e.preventDefault();
    const form = e.target;
    try {
        await api.updateListing(listingId, {
            id: listingId,
            ownerId: currentUser.userId,
            title: form.title.value,
            description: form.description.value,
            dailyRate: parseFloat(form.dailyRate.value),
            city: form.city.value || null,
            district: form.district.value || null,
            enabled: form.enabled.checked
        });
        showToast('Listing updated!');
        closeModal();
        loadMyListings();
    } catch (err) {
        showToast('Failed to update listing', 'error');
    }
}

// Bookings
async function loadBookings() {
    try {
        const bookings = await api.getBookingsByRenter(currentUser.userId);
        renderBookings(bookings);
    } catch (e) {
        showToast('Failed to load bookings', 'error');
    }
}

function renderBookings(bookings) {
    const container = document.getElementById('bookings-container');
    if (!bookings.length) {
        container.innerHTML = '<p class="empty">No bookings found</p>';
        return;
    }
    container.innerHTML = bookings.map(b => `
        <div class="list-item">
            <div class="list-item-info">
                <strong>Booking #${b.id}</strong>
                <span class="badge badge-${getStatusClass(b.status)}">${b.status}</span>
            </div>
            <div class="list-item-details">
                <span>Request ID: ${b.requestId}</span>
                <span class="price">€${b.price.toFixed(2)}</span>
            </div>
            <div class="list-item-actions">
                ${b.status === 'PENDING' ? `<button class="btn btn-success btn-sm" onclick="updateBookingStatus(${b.id}, 'PAID')">Pay</button><button class="btn btn-danger btn-sm" onclick="cancelBooking(${b.id})">Cancel</button>` : ''}
                ${b.status === 'PAID' ? `<button class="btn btn-danger btn-sm" onclick="cancelBooking(${b.id})">Cancel</button>` : ''}
            </div>
        </div>
    `).join('');
}

async function updateBookingStatus(id, status) {
    try {
        await api.updateBookingStatus(id, status);
        showToast(`Booking ${status.toLowerCase()}`);
        loadBookings();
    } catch (e) {
        showToast('Failed to update booking', 'error');
    }
}

async function cancelBooking(id) {
    if (!confirm('Cancel this booking?')) return;
    try {
        await api.cancelBooking(id);
        showToast('Booking cancelled');
        loadBookings();
    } catch (e) {
        showToast('Failed to cancel booking', 'error');
    }
}

// Requests
async function loadRequestsPage() {
    try {
        const listings = await api.getListings();
        const myListings = listings.filter(l => l.ownerId === currentUser.userId);
        const select = document.getElementById('listing-select');
        select.innerHTML = myListings.length ? myListings.map(l => `<option value="${l.id}">${escapeHtml(l.title)}</option>`).join('') : '<option value="">No listings</option>';
        select.onchange = () => loadRequests(select.value);
        if (myListings.length) loadRequests(myListings[0].id);
        else document.getElementById('requests-container').innerHTML = '<p class="empty">Create a listing first</p>';
    } catch (e) {
        showToast('Failed to load listings', 'error');
    }
}

async function loadRequests(listingId) {
    if (!listingId) return;
    try {
        const requests = await api.getRequestsByListing(listingId);
        renderRequests(requests);
    } catch (e) {
        showToast('Failed to load requests', 'error');
    }
}

function renderRequests(requests) {
    const container = document.getElementById('requests-container');
    if (!requests.length) {
        container.innerHTML = '<p class="empty">No requests for this listing</p>';
        return;
    }
    container.innerHTML = requests.map(r => `
        <div class="list-item">
            <div class="list-item-info">
                <strong>Request #${r.id}</strong>
                <span>From User #${r.requesterId}</span>
            </div>
            <div class="list-item-details">
                <span>Start: Day ${r.initialDate}</span>
                <span>Duration: ${r.duration} days</span>
                ${r.note ? `<span class="note">"${escapeHtml(r.note)}"</span>` : ''}
            </div>
            <div class="list-item-actions">
                <button class="btn btn-success btn-sm" onclick="acceptRequest(${JSON.stringify(r).replace(/"/g, '&quot;')})">Accept</button>
            </div>
        </div>
    `).join('');
}

async function acceptRequest(request) {
    try {
        await api.acceptRequest(request);
        showToast('Request accepted! Booking created.');
        loadRequests(request.listingId);
    } catch (e) {
        showToast('Failed to accept request', 'error');
    }
}

// My Requests (for renters)
async function loadMyRequests() {
    try {
        const requests = await api.getMyRequests(currentUser.userId);
        renderMyRequests(requests);
    } catch (e) {
        showToast('Failed to load requests', 'error');
    }
}

function renderMyRequests(requests) {
    const container = document.getElementById('my-requests-container');
    if (!requests.length) {
        container.innerHTML = '<p class="empty">No pending requests</p>';
        return;
    }
    container.innerHTML = requests.map(r => `
        <div class="list-item">
            <div class="list-item-info">
                <strong>Request #${r.id}</strong>
                <span>Listing #${r.listingId}</span>
            </div>
            <div class="list-item-details">
                <span>Start: Day ${r.initialDate}</span>
                <span>Duration: ${r.duration} days</span>
                ${r.note ? `<span class="note">"${escapeHtml(r.note)}"</span>` : ''}
            </div>
            <div class="list-item-actions">
                <button class="btn btn-danger btn-sm" onclick="cancelRequest(${r.id})">Cancel</button>
            </div>
        </div>
    `).join('');
}

async function cancelRequest(requestId) {
    if (!confirm('Cancel this request?')) return;
    try {
        await api.cancelRequest(requestId);
        showToast('Request cancelled');
        loadMyRequests();
    } catch (e) {
        showToast('Failed to cancel request', 'error');
    }
}

async function loadRenterDashboard() {
    if (!currentUser) return;

    try {
        document.getElementById('renter-dashboard-page').classList.add('loading');
        const dashboard = await api.getRenterDashboard(currentUser.userId);
        updateRenterStats(dashboard);
        renderRentalLists(dashboard);
        await loadAllRentals();

    } catch (error) {
        console.error('Error loading renter dashboard:', error);
        showToast('Failed to load dashboard', 'error');
    } finally {
        document.getElementById('renter-dashboard-page').classList.remove('loading');
    }
}
function updateRenterStats(dashboard) {
    document.getElementById('total-rentals').textContent = dashboard.totalRentals || 0;
    document.getElementById('active-rentals').textContent = dashboard.activeRentalsCount || 0;
    document.getElementById('pending-rentals').textContent = dashboard.pendingRentalsCount || 0;
    document.getElementById('total-spent').textContent = `€${(dashboard.totalSpent || 0).toFixed(2)}`;
    document.getElementById('monthly-spent').textContent = `€${(dashboard.monthlySpent || 0).toFixed(2)}`;
}

function renderRentalLists(dashboard) {
    const activeList = document.getElementById('active-rentals-list');
    if (dashboard.activeRentals && dashboard.activeRentals.length > 0) {
        activeList.innerHTML = dashboard.activeRentals.map(rental => renderRentalItem(rental)).join('');
    } else {
        activeList.innerHTML = '<div class="empty">No active rentals</div>';
    }
    const pendingList = document.getElementById('pending-rentals-list');
    if (dashboard.pendingRentals && dashboard.pendingRentals.length > 0) {
        pendingList.innerHTML = dashboard.pendingRentals.map(rental => renderRentalItem(rental)).join('');
    } else {
        pendingList.innerHTML = '<div class="empty">No pending rentals</div>';
    }
}
function renderRentalItem(rental) {
    return `
        <div class="list-item">
            <div class="list-item-info">
                <strong>${escapeHtml(rental.listingTitle)}</strong>
                <span class="badge badge-${getStatusClass(rental.status)}">${rental.status}</span>
            </div>
            <div class="list-item-details">
                <span>€${rental.price.toFixed(2)}</span>
                <span>${formatDate(rental.startDate)} - ${formatDate(rental.endDate)}</span>
                <span>Owner: ${escapeHtml(rental.ownerName)}</span>
            </div>
            <div class="list-item-actions">
                ${rental.status === 'PENDING' ?
                    `<button class="btn btn-success btn-sm" onclick="payBooking(${rental.bookingId})">Pay Now</button>` : ''}
                <button class="btn btn-info btn-sm" onclick="showRentalDetails(${rental.bookingId})">Details</button>
                ${rental.status === 'PAID' || rental.status === 'PENDING' ?
                    `<button class="btn btn-danger btn-sm" onclick="cancelBooking(${rental.bookingId})">Cancel</button>` : ''}
            </div>
        </div>
    `;
}

async function loadAllRentals() {
    try {
        const rentals = await api.getRenterDetailedBookings(currentUser.userId);
        renderAllRentals(rentals);
    } catch (error) {
        console.error('Error loading all rentals:', error);
        document.getElementById('all-rentals').innerHTML = '<div class="empty">Failed to load rentals</div>';
    }
}

function renderAllRentals(rentals) {
    const container = document.getElementById('all-rentals');

    if (!rentals || rentals.length === 0) {
        container.innerHTML = '<div class="empty">No rentals found</div>';
        return;
    }

    container.innerHTML = rentals.map(rental => `
        <div class="list-item">
            <div class="list-item-info">
                <strong>${escapeHtml(rental.listingTitle)}</strong>
                <span class="badge badge-${getStatusClass(rental.status)}">${rental.status}</span>
            </div>
            <div class="list-item-details">
                <span class="price">€${rental.price.toFixed(2)}</span>
                <span>${formatDate(rental.startDate)} - ${formatDate(rental.endDate)}</span>
                <span>Booking #${rental.bookingId}</span>
                <span>Owner: ${escapeHtml(rental.ownerName)}</span>
            </div>
            <div class="list-item-actions">
                <button class="btn btn-info btn-sm" onclick="showRentalDetails(${rental.bookingId})">View</button>
                <button class="btn btn-secondary btn-sm" onclick="contactOwner('${escapeHtml(rental.ownerEmail)}')">Contact</button>
            </div>
        </div>
    `).join('');
}


// Helpers
function getStatusClass(status) {
    const classes = { PENDING: 'warning', PAID: 'info', ACCEPTED: 'success', DECLINED: 'danger', CANCELLED: 'danger', COMPLETED: 'success' };
    return classes[status] || 'secondary';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
