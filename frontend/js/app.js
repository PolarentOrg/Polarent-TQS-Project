// State
let currentUser = null;
let allListings = [];
let adminListings = [];

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
    
    // Mostrar/ocultar link de admin baseado no role
    const adminNav = document.getElementById('admin-nav');
    if (adminNav) {
        adminNav.style.display = currentUser.role === 'ADMIN' ? 'block' : 'none';
    }
    
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
    else if (page === 'admin') loadAdminPage();
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

async function renderListings(listings, containerId, isOwner) {
    const container = document.getElementById(containerId);
    if (!listings.length) {
        container.innerHTML = '<p class="empty">No listings found</p>';
        return;
    }
    
    // Fetch owner details for each listing
    const listingsWithOwners = await Promise.all(
        listings.map(async (listing) => {
            try {
                const owner = await adminApi.getUserById(listing.ownerId);
                return { ...listing, owner };
            } catch {
                return { ...listing, owner: null };
            }
        })
    );
    
    container.innerHTML = listingsWithOwners.map(l => `
        <div class="card">
            <div class="card-header">
                <h3>${escapeHtml(l.title)}</h3>
                <span class="badge ${l.enabled ? 'badge-success' : 'badge-warning'}">${l.enabled ? 'Available' : 'Unavailable'}</span>
            </div>
            ${l.city || l.district ? `<p class="location">📍 ${escapeHtml([l.city, l.district].filter(Boolean).join(', '))}</p>` : ''}
            ${l.owner ? `<p class="owner">👤 ${escapeHtml(l.owner.firstName)} ${escapeHtml(l.owner.lastName)}</p>` : ''}
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
        const details = await api.getEquipmentDetails(listingId);
        if (!details) {
            showToast('Equipment not found', 'error');
            return;
        }
        openModal(`
            <h3>${escapeHtml(details.title)}</h3>
            <div class="equipment-details">
                <p><strong>Description:</strong> ${escapeHtml(details.description || 'No description available')}</p>
                <p><strong>Daily Rate:</strong> €${details.dailyRate.toFixed(2)}/day</p>
                ${details.city || details.district ? `<p><strong>Location:</strong> ${escapeHtml([details.city, details.district].filter(Boolean).join(', '))}</p>` : ''}
                <p><strong>Owner:</strong> ${escapeHtml(details.ownerName)}</p>
                <p><strong>Contact:</strong> ${escapeHtml(details.ownerEmail)}</p>
                <p><strong>Status:</strong> <span class="badge ${details.available ? 'badge-success' : 'badge-warning'}">${details.available ? 'Available' : 'Unavailable'}</span></p>
                <p><strong>Listed:</strong> ${new Date(details.createdAt).toLocaleDateString()}</p>
            </div>
            <div class="modal-actions">
                ${details.available ? `<button class="btn btn-primary" onclick="closeModal(); showRentModal(${details.id})">Rent This Item</button>` : ''}
                <button class="btn btn-secondary" onclick="closeModal()">Close</button>
            </div>
        `);
    } catch (e) {
        showToast('Failed to load equipment details', 'error');
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
        const [renterBookings, ownerBookings] = await Promise.all([
            api.getBookingsByRenter(currentUser.userId),
            api.getBookingsByOwner(currentUser.userId)
        ]);
        renderBookings(renterBookings, ownerBookings);
    } catch (e) {
        showToast('Failed to load bookings', 'error');
    }
}

function renderBookings(renterBookings, ownerBookings) {
    const container = document.getElementById('bookings-container');
    
    let html = '';
    
    if (renterBookings.length > 0) {
        html += '<h3>My Rental Bookings</h3>';
        html += renterBookings.map(b => `
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
    
    if (ownerBookings.length > 0) {
        html += '<h3>Bookings for My Listings</h3>';
        html += ownerBookings.map(b => `
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
                    ${b.status === 'PENDING' ? `<button class="btn btn-success btn-sm" onclick="updateBookingStatus(${b.id}, 'ACCEPTED')">Accept</button><button class="btn btn-danger btn-sm" onclick="declineBooking(${b.id})">Decline</button>` : ''}
                </div>
            </div>
        `).join('');
    }
    
    if (!renterBookings.length && !ownerBookings.length) {
        html = '<p class="empty">No bookings found</p>';
    }
    
    container.innerHTML = html;
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

async function declineBooking(id) {
    if (!confirm('Decline this booking?')) return;
    try {
        await api.declineBooking(id);
        showToast('Booking declined');
        loadBookings();
    } catch (e) {
        showToast('Failed to decline booking', 'error');
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

// Admin functions
async function loadAdminPage() {
    if (currentUser.role !== 'ADMIN') {
        showToast('Access denied', 'error');
        return;
    }
    try {
        const users = await adminApi.getAllUsers();
        renderUsers(users);
        await loadAllListingsForAdmin();
    } catch (e) {
        showToast('Failed to load admin data', 'error');
    }
}

function renderUsers(users) {
    const container = document.getElementById('users-container');
    if (!users.length) {
        container.innerHTML = '<p class="empty">No users found</p>';
        return;
    }
    container.innerHTML = users.map(u => `
        <div class="list-item">
            <div class="list-item-info">
                <strong>${escapeHtml(u.firstName)} ${escapeHtml(u.lastName)}</strong>
                <span class="badge badge-${u.role === 'ADMIN' ? 'info' : 'secondary'}">${u.role}</span>
                <span class="badge badge-${u.active ? 'success' : 'danger'}">${u.active ? 'Active' : 'Inactive'}</span>
            </div>
            <div class="list-item-details">
                <span>${escapeHtml(u.email)}</span>
                <span>ID: ${u.id}</span>
            </div>
            <div class="list-item-actions">
                ${u.active ? 
                    `<button class="btn btn-warning btn-sm" onclick="deactivateUser(${u.id})">Deactivate</button>` :
                    `<button class="btn btn-success btn-sm" onclick="activateUser(${u.id})">Activate</button>`
                }
                <button class="btn btn-danger btn-sm" onclick="deleteUser(${u.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

async function activateUser(id) {
    try {
        await adminApi.activateUser(id);
        showToast('User activated');
        loadAdminPage();
    } catch (e) {
        showToast('Failed to activate user', 'error');
    }
}

async function deactivateUser(id) {
    try {
        await adminApi.deactivateUser(id);
        showToast('User deactivated');
        loadAdminPage();
    } catch (e) {
        showToast('Failed to deactivate user', 'error');
    }
}

async function deleteUser(id) {
    if (!confirm('Delete this user? This action cannot be undone.')) return;
    try {
        await adminApi.deleteUser(id);
        showToast('User deleted');
        loadAdminPage();
    } catch (e) {
        showToast('Failed to delete user', 'error');
    }
}

async function loadAllListingsForAdmin() {
    if (currentUser.role !== 'ADMIN') {
        console.error('❌ Access denied - User role:', currentUser.role);
        showToast('Access denied. Admin privileges required.', 'error');
        return;
    }

    try {
        console.log('🔍 Fetching admin listings...');
        console.log('📡 API URL:', `${API_BASE || '/api'}/listings/admin/all`);

        adminListings = await api.getAllListingsForAdmin();

        console.log('✅ Listings loaded:', adminListings);
        console.log('📊 Total listings:', adminListings.length);

        renderAdminListings(adminListings);
    } catch (error) {
        console.error('❌ Failed to load listings:', error);
        console.error('Error details:', error.message, error.stack);
        showToast(`Failed to load listings: ${error.message}`, 'error');
    }
}

// Renderizar listagens do admin
function renderAdminListings(listings) {
    const container = document.getElementById('admin-listings-container');

    if (!listings || listings.length === 0) {
        container.innerHTML = '<div class="empty">No listings found</div>';
        return;
    }

    container.innerHTML = listings.map(listing => {
        const isEnabled = listing.enabled === true;

        return `
            <div class="admin-listing-card ${!isEnabled ? 'disabled' : ''}">
                <div class="listing-meta">
                    <span>ID: ${listing.id}</span>
                    <span class="badge ${isEnabled ? 'badge-success' : 'badge-warning'}">
                        ${isEnabled ? 'Enabled' : 'Disabled'}
                    </span>
                    <span>Owner ID: ${listing.ownerId}</span>
                    <span>Created: ${new Date(listing.createdAt).toLocaleDateString()}</span>
                </div>

                <div class="listing-content">
                    <h4>${escapeHtml(listing.title)}</h4>
                    <p>${escapeHtml(listing.description || 'No description provided')}</p>
                    <p><strong>Daily Rate:</strong> €${listing.dailyRate.toFixed(2)}</p>
                    <p><strong>Location:</strong> ${escapeHtml(listing.city || 'N/A')}, ${escapeHtml(listing.district || 'N/A')}</p>
                </div>

                <div class="listing-owner">
                    <strong>Listing Owner:</strong> User #${listing.ownerId}
                    <br>
                    <small>Listed on: ${new Date(listing.createdAt).toLocaleString()}</small>
                </div>

                <div class="admin-listing-actions">
                    <button class="btn btn-info btn-sm" onclick="viewAdminListingDetails(${listing.id})">
                        View Details
                    </button>
                    <button class="btn btn-danger btn-sm"
                            onclick="removeInappropriateListing(${listing.id}, '${escapeHtml(listing.title)}')">
                        Remove Listing
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

// Filtrar listagens do admin
function filterAdminListings() {
    const searchInput = document.getElementById('admin-search-input');
    const searchTerm = searchInput ? searchInput.value.toLowerCase() : '';

    if (!searchTerm) {
        renderAdminListings(adminListings);
        return;
    }

    const filtered = adminListings.filter(listing =>
        listing.title.toLowerCase().includes(searchTerm) ||
        listing.description?.toLowerCase().includes(searchTerm) ||
        listing.city?.toLowerCase().includes(searchTerm) ||
        listing.district?.toLowerCase().includes(searchTerm)
    );

    renderAdminListings(filtered);
}

// Ver detalhes de uma listagem (modal)
async function viewAdminListingDetails(listingId) {
    try {
        const listing = adminListings.find(l => l.id === listingId) || await api.getListingById(listingId);

        if (!listing) {
            showToast('Listing not found', 'error');
            return;
        }

        openModal(`
            <h3>📋 Listing Details (Admin View)</h3>
            <div class="listing-details">
                <p><strong>Listing ID:</strong> ${listing.id}</p>
                <p><strong>Title:</strong> ${escapeHtml(listing.title)}</p>
                <p><strong>Description:</strong> ${escapeHtml(listing.description || 'No description')}</p>
                <p><strong>Daily Rate:</strong> €${listing.dailyRate.toFixed(2)}</p>
                <p><strong>Status:</strong>
                    <span class="badge ${listing.enabled ? 'badge-success' : 'badge-warning'}">
                        ${listing.enabled ? 'ENABLED' : 'DISABLED'}
                    </span>
                </p>
                <p><strong>Location:</strong> ${escapeHtml(listing.city || 'N/A')}, ${escapeHtml(listing.district || 'N/A')}</p>
                <p><strong>Owner ID:</strong> ${listing.ownerId}</p>
                <p><strong>Created:</strong> ${new Date(listing.createdAt).toLocaleString()}</p>
                <p><strong>Last Updated:</strong> ${new Date(listing.updatedAt).toLocaleString()}</p>
            </div>

            <div class="warning-box">
                <strong>⚠️ Admin Action</strong>
                <p>You are about to perform an administrative action on this listing.</p>
            </div>

            <div class="modal-actions">
                <button class="btn btn-danger"
                        onclick="closeModal(); removeInappropriateListing(${listing.id}, '${escapeHtml(listing.title)}')">
                    Remove This Listing
                </button>
                <button class="btn btn-secondary" onclick="closeModal()">
                    Close
                </button>
            </div>
        `);
    } catch (error) {
        console.error('Failed to load listing details:', error);
        showToast('Failed to load listing details', 'error');
    }
}

// Remover listagem inadequada
async function removeInappropriateListing(listingId, listingTitle) {
    if (currentUser.role !== 'ADMIN') {
        showToast('Access denied. Admin privileges required.', 'error');
        return;
    }

    if (!confirm(`🚨 REMOVE LISTING\n\nAre you sure you want to remove this listing?\n\n"${listingTitle}"\n\nThis action is permanent and cannot be undone.`)) {
        showToast('Action cancelled', 'info');
        return;
    }

    try {
        // USAR api.removeInappropriateListing (não adminListingApi)
        await api.removeInappropriateListing(listingId);
        showToast(`✅ Listing "${listingTitle}" has been removed`, 'success');

        // Atualizar a lista
        await loadAllListingsForAdmin();
    } catch (error) {
        console.error('Failed to remove listing:', error);
        showToast('Failed to remove listing', 'error');
    }
}

