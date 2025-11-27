let useLocalStorage = false;
let isReady = false;

document.addEventListener('DOMContentLoaded', () => {
    const submitBtn = document.querySelector('#addForm button');
    submitBtn.disabled = true;
    submitBtn.textContent = "Connecting...";

    // Check if API is available
    fetch('/api/fleet')
        .then(response => {
            if (!response.ok || !response.headers.get("content-type")?.includes("application/json")) {
                throw new Error("API unavailable");
            }
            return response.json();
        })
        .then(data => {
            isReady = true;
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-plus"></i> Add Vehicle';
            renderTable(data);
        })
        .catch(err => {
            console.log("API not found, switching to Demo Mode (LocalStorage)");
            useLocalStorage = true;
            isReady = true;
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-plus"></i> Add Vehicle';
            showDemoBanner();
            loadFleet();
        });
});

document.getElementById('addForm').addEventListener('submit', function(e) {
    e.preventDefault();
    if (!isReady) return;
    
    const type = document.getElementById('type').value;
    const id = document.getElementById('id').value;
    const brand = document.getElementById('brand').value;
    const extra = document.getElementById('extra').value;

    if (useLocalStorage) {
        const fleet = getLocalFleet();
        if (fleet.some(v => v.id === id)) {
            alert("ID already exists");
            return;
        }
        fleet.push({ id, type, brand, extra, rented: false });
        saveLocalFleet(fleet);
        alert("Added (Demo Mode)");
        document.getElementById('addForm').reset();
        loadFleet();
    } else {
        fetch('/api/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `type=${type}&id=${id}&brand=${brand}&extra=${extra}`
        })
        .then(response => {
            if (!response.ok) throw new Error("Server Error");
            return response.text();
        })
        .then(msg => {
            alert(msg);
            if(msg === 'Added') {
                document.getElementById('addForm').reset();
                loadFleet();
            }
        })
        .catch(err => {
            alert("Error connecting to server. Switching to Demo Mode.");
            useLocalStorage = true;
            showDemoBanner();
        });
    }
});

function loadFleet() {
    if (useLocalStorage) {
        renderTable(getLocalFleet());
    } else {
        fetch('/api/fleet')
        .then(response => response.json())
        .then(data => renderTable(data));
    }
}

function renderTable(data) {
    const tbody = document.querySelector('#fleetTable tbody');
    tbody.innerHTML = '';
    
    data.forEach(v => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${v.id}</td>
            <td>${v.type}</td>
            <td>${v.brand}</td>
            <td>${v.extra}</td>
            <td><span class="status ${v.rented ? 'rented' : 'available'}">${v.rented ? 'Rented' : 'Available'}</span></td>
            <td>
                ${v.rented 
                    ? `<button onclick="returnVehicle('${v.id}')" class="btn-action return">Return</button>`
                    : `<button onclick="rentVehicle('${v.id}')" class="btn-action rent">Rent</button>`
                }
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function rentVehicle(id) {
    if (useLocalStorage) {
        const fleet = getLocalFleet();
        const v = fleet.find(v => v.id === id);
        if (v) {
            v.rented = true;
            saveLocalFleet(fleet);
            alert("Rented (Demo Mode)");
            loadFleet();
        }
    } else {
        fetch('/api/rent', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `id=${id}`
        })
        .then(response => response.text())
        .then(msg => {
            alert(msg);
            loadFleet();
        });
    }
}

function returnVehicle(id) {
    if (useLocalStorage) {
        const fleet = getLocalFleet();
        const v = fleet.find(v => v.id === id);
        if (v) {
            v.rented = false;
            saveLocalFleet(fleet);
            alert("Returned (Demo Mode)");
            loadFleet();
        }
    } else {
        fetch('/api/return', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `id=${id}`
        })
        .then(response => response.text())
        .then(msg => {
            alert(msg);
            loadFleet();
        });
    }
}

// Local Storage Helpers
function getLocalFleet() {
    return JSON.parse(localStorage.getItem('vehicle_fleet') || '[]');
}

function saveLocalFleet(fleet) {
    localStorage.setItem('vehicle_fleet', JSON.stringify(fleet));
}

function showDemoBanner() {
    const banner = document.createElement('div');
    banner.style.background = '#f39c12';
    banner.style.color = 'white';
    banner.style.textAlign = 'center';
    banner.style.padding = '10px';
    banner.style.fontWeight = 'bold';
    banner.innerHTML = '⚠️ Demo Mode: Running in browser (Offline). Data is saved locally.';
    document.body.insertBefore(banner, document.body.firstChild);
}
