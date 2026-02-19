console.log("Smart Contact Manager - JavaScript Loaded");

// ============ SIDEBAR TOGGLE FUNCTION (Before Login) ============
function openNav() {
	const sidebar = document.getElementById("mySidebar");
	const main = document.getElementById("main");

	if (sidebar && main) {
		sidebar.style.width = "250px";
		main.style.marginLeft = "250px";
		document.body.style.overflow = "hidden"; // Prevent body scroll when sidebar is open
	}
}

function closeNav() {
	const sidebar = document.getElementById("mySidebar");
	const main = document.getElementById("main");

	if (sidebar && main) {
		sidebar.style.width = "0";
		main.style.marginLeft = "0";
		document.body.style.overflow = "auto"; // Re-enable body scroll
	}
}

// Close sidebar when a link is clicked
document.addEventListener("DOMContentLoaded", function() {
	const sidebarLinks = document.querySelectorAll(".sidebar1 a");
	sidebarLinks.forEach(link => {
		link.addEventListener("click", closeNav);
	});

	// Close sidebar when clicking outside of it
	document.addEventListener("click", function(event) {
		const sidebar = document.getElementById("mySidebar");
		const openBtn = document.querySelector(".openbtn");

		if (sidebar && !sidebar.contains(event.target) && !openBtn?.contains(event.target)) {
			if (sidebar.style.width !== "0") {
				// Only close if clicking outside
				if (event.target.closest(".navbar") || event.target.closest(".banner") || event.target.closest("main")) {
					closeNav();
				}
			}
		}
	});

	// Handle keyboard escape key to close sidebar
	document.addEventListener("keydown", function(event) {
		if (event.key === "Escape") {
			closeNav();
		}
	});
});

// ============ SIDEBAR TOGGLE FUNCTION (After Login) ============
const toggleSidebar = () => {
	const sidebar = $(".sidebar");
	const content = $(".content");

	if (sidebar.is(":visible")) {
		sidebar.fadeOut(300);
		content.animate({ marginLeft: "0%" }, 300);
	} else {
		sidebar.fadeIn(300);
		content.animate({ marginLeft: "20%" }, 300);
	}
};

// ============ SEARCH FUNCTIONALITY ============
const search = () => {
	const query = $("#search-input").val().trim();
	const searchResult = $(".search-result");

	if (query === "") {
		searchResult.hide();
		return;
	}

	// Show loading state
	searchResult.html('<div class="p-3 text-center"><small>Searching...</small></div>');
	searchResult.show();

	// Build search URL
	const protocol = window.location.protocol;
	const host = window.location.host;
	const searchUrl = `${protocol}//${host}/search/${encodeURIComponent(query)}`;

	// Fetch search results
	fetch(searchUrl, {
		headers: {
			"Accept": "application/json"
		}
	})
		.then((response) => {
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.json();
		})
		.then((data) => {
			if (!data || data.length === 0) {
				searchResult.html('<div class="p-3 text-center"><small>No contacts found</small></div>');
				return;
			}

			let html = '<div class="list-group">';
			data.forEach(contact => {
				const contactId = contact.cId || contact.id;
				const contactName = contact.name || "Unknown";
				html += `<a href="/user/contact/${contactId}" class="list-group-item list-group-item-action" style="text-decoration: none; transition: all 0.2s;">
					<div>
						<strong>${contactName}</strong>
						${contact.email ? `<br><small class="text-muted">${contact.email}</small>` : ''}
					</div>
				</a>`;
			});
			html += '</div>';

			searchResult.html(html);
			searchResult.show();
		})
		.catch((error) => {
			console.error("Search error:", error);
			searchResult.html('<div class="p-3 text-center"><small class="text-danger">Error searching contacts</small></div>');
		});
};

// ============ DEBOUNCE SEARCH ============
let searchTimeout;
document.addEventListener("DOMContentLoaded", function() {
	const searchInput = document.getElementById("search-input");
	if (searchInput) {
		searchInput.addEventListener("input", () => {
			clearTimeout(searchTimeout);
			searchTimeout = setTimeout(search, 300); // Wait 300ms after user stops typing
		});

		// Close search results when clicking outside
		document.addEventListener("click", function(event) {
			if (!event.target.closest(".search-container")) {
				$(".search-result").hide();
			}
		});
	}
});

// ============ RESPONSIVE NAVBAR ============
document.addEventListener("DOMContentLoaded", function() {
	const navLinks = document.querySelectorAll(".navbar-collapse a");
	const navbarCollapse = document.querySelector(".navbar-collapse");
	const navbarToggler = document.querySelector(".navbar-toggler");

	navLinks.forEach(link => {
		link.addEventListener("click", function() {
			// Auto-close navbar on mobile after clicking a link
			if (window.innerWidth < 992) {
				// Bootstrap 5 uses collapse API
				if (navbarCollapse?.classList.contains("show")) {
					navbarToggler?.click();
				}
			}
		});
	});
});

// ============ SMOOTH SCROLL ============
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
	anchor.addEventListener('click', function (e) {
		e.preventDefault();
		const target = document.querySelector(this.getAttribute('href'));
		if (target) {
			target.scrollIntoView({ behavior: 'smooth' });
		}
	});
});

// ============ UTILITY FUNCTIONS ============

// Disable form submission on Enter for search
document.addEventListener("DOMContentLoaded", function() {
	const searchForm = document.querySelector(".search-container form");
	if (searchForm) {
		searchForm.addEventListener("submit", (e) => e.preventDefault());
	}
});

// Add active class to current nav link
document.addEventListener("DOMContentLoaded", function() {
	const currentPath = window.location.pathname;
	const navLinks = document.querySelectorAll(".navbar-nav a.nav-link");

	navLinks.forEach(link => {
		if (link.getAttribute("href") === currentPath) {
			link.classList.add("active");
		}
	});
});
