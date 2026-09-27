/**
 * BookNest - Smart Online Book Explorer
 * Frontend client script communicating with Spring Boot REST API and MySQL.
 */

// Centralized API Base URL configuration
const API_BASE_URL = "https://booknest-backend-3ilg.onrender.com/api";
// Application state
let currentBooks = [];
let currentCategory = "All";
let currentSort = "featured";
let favoriteBookIds = [];

// Toast notification helper
function showToast(message) {
  const toastEl = document.getElementById("actionToast");
  const toastBody = document.getElementById("actionToastBody");
  if (!toastEl || !toastBody || typeof bootstrap === "undefined") return;

  toastBody.textContent = message;
  const toastInstance = bootstrap.Toast.getOrCreateInstance(toastEl);
  toastInstance.show();
}

// Format currency display
function formatPrice(price) {
  if (price === null || price === undefined) return "Rs. 0.00";
  return "Rs. " + Number(price).toFixed(2);
}

// Load books from Spring Boot REST API
async function loadBooks() {
  const container = document.getElementById("bookCollectionContainer");
  const backendNotice = document.getElementById("backendErrorNotice");
  const emptyNotice = document.getElementById("bookEmptyNotice");
  if (!container) return;

  try {
    let url = `${API_BASE_URL}/books?sort=${encodeURIComponent(currentSort)}`;
    if (currentCategory && currentCategory !== "All") {
      url += `&category=${encodeURIComponent(currentCategory)}`;
    }

    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Server returned HTTP status ${response.status}`);
    }

    const json = await response.json();
    currentBooks = json.data || [];

    if (backendNotice) backendNotice.classList.add("d-none");

    renderBooks(currentBooks);
    updateCategoryCounts();
  } catch (error) {
    console.error("Unable to load books from backend:", error);
    container.innerHTML = "";
    if (backendNotice) {
      backendNotice.classList.remove("d-none");
    }
  }
}

// Render book cards to the DOM
function renderBooks(books) {
  const container = document.getElementById("bookCollectionContainer");
  const emptyNotice = document.getElementById("bookEmptyNotice");
  if (!container) return;

  container.innerHTML = "";

  if (!books || books.length === 0) {
    if (emptyNotice) emptyNotice.classList.remove("d-none");
    return;
  }

  if (emptyNotice) emptyNotice.classList.add("d-none");

  books.forEach(book => {
    const isFav = favoriteBookIds.includes(Number(book.id));
    const isAvailable = Boolean(book.availability);
    const availabilityClass = isAvailable ? "availability-available" : "availability-not-available";
    const availabilityText = isAvailable ? "Available" : "Not Available";

    const col = document.createElement("div");
    col.className = "col-md-6 col-lg-4";

    col.innerHTML = `
      <div class="book-card">
        <div class="book-cover-banner">
          <span class="book-category-badge">${escapeHtml(book.category)}</span>
          <span class="book-availability-badge ${availabilityClass}">${availabilityText}</span>
        </div>
        <div class="book-card-body">
          <h3 class="book-title">${escapeHtml(book.title)}</h3>
          <div class="book-author">By ${escapeHtml(book.author)}</div>
          <p class="book-description">${escapeHtml(book.description || "")}</p>
          <div class="book-meta">
            <span class="book-price">${formatPrice(book.price)}</span>
            <span class="book-rating">Rating: ${book.rating ? Number(book.rating).toFixed(1) : "4.5"}/5</span>
          </div>
          <div class="book-card-actions">
            <button type="button" class="btn btn-outline-primary btn-sm" onclick="openBookDetails(${book.id})">
              View Details
            </button>
            <button type="button" class="btn ${isFav ? 'btn-secondary' : 'btn-outline-secondary'} btn-sm" onclick="toggleFavorite(${book.id})">
              ${isFav ? 'Remove Favorite' : 'Add to Favorites'}
            </button>
          </div>
        </div>
      </div>
    `;

    container.appendChild(col);
  });
}

// Fetch favorite book IDs and list from MySQL via REST API
async function loadFavorites() {
  const container = document.getElementById("favoritesListContainer");
  const emptyState = document.getElementById("favoritesEmptyState");

  try {
    // 1. Fetch favorite book IDs
    const idsResponse = await fetch(`${API_BASE_URL}/favorites/ids`);
    if (idsResponse.ok) {
      const idsJson = await idsResponse.json();
      favoriteBookIds = idsJson.data || [];
    }

    // 2. Fetch full favorite book objects
    if (container) {
      const favResponse = await fetch(`${API_BASE_URL}/favorites`);
      if (favResponse.ok) {
        const favJson = await favResponse.json();
        const favBooks = favJson.data || [];

        container.innerHTML = "";

        if (favBooks.length === 0) {
          if (emptyState) emptyState.classList.remove("d-none");
        } else {
          if (emptyState) emptyState.classList.add("d-none");

          favBooks.forEach(book => {
            const isAvailable = Boolean(book.availability);
            const availabilityClass = isAvailable ? "availability-available" : "availability-not-available";
            const availabilityText = isAvailable ? "Available" : "Not Available";

            const col = document.createElement("div");
            col.className = "col-md-6 col-lg-4";

            col.innerHTML = `
              <div class="book-card">
                <div class="book-cover-banner">
                  <span class="book-category-badge">${escapeHtml(book.category)}</span>
                  <span class="book-availability-badge ${availabilityClass}">${availabilityText}</span>
                </div>
                <div class="book-card-body">
                  <h3 class="book-title">${escapeHtml(book.title)}</h3>
                  <div class="book-author">By ${escapeHtml(book.author)}</div>
                  <p class="book-description">${escapeHtml(book.description || "")}</p>
                  <div class="book-meta">
                    <span class="book-price">${formatPrice(book.price)}</span>
                    <span class="book-rating">Rating: ${book.rating ? Number(book.rating).toFixed(1) : "4.5"}/5</span>
                  </div>
                  <div class="book-card-actions">
                    <button type="button" class="btn btn-outline-primary btn-sm" onclick="openBookDetails(${book.id})">
                      View Details
                    </button>
                    <button type="button" class="btn btn-secondary btn-sm" onclick="toggleFavorite(${book.id})">
                      Remove Favorite
                    </button>
                  </div>
                </div>
              </div>
            `;
            container.appendChild(col);
          });
        }
      }
    }
  } catch (error) {
    console.warn("Unable to fetch favorites from MySQL:", error);
  }
}

// Toggle Favorite state via Spring Boot REST API
async function toggleFavorite(bookId) {
  const numericId = Number(bookId);
  const isFav = favoriteBookIds.includes(numericId);

  try {
    if (isFav) {
      const response = await fetch(`${API_BASE_URL}/favorites/${numericId}`, {
        method: "DELETE"
      });
      if (response.ok) {
        showToast("Favorite removed.");
      }
    } else {
      const response = await fetch(`${API_BASE_URL}/favorites/${numericId}`, {
        method: "POST"
      });
      if (response.ok) {
        showToast("Favorite added.");
      }
    }

    // Refresh state from MySQL database
    await loadFavorites();
    renderBooks(currentBooks);
    updateModalFavoriteButton(numericId);
  } catch (error) {
    console.error("Failed to update favorite in MySQL:", error);
    showToast("Unable to update favorite. Please check backend connection.");
  }
}

// Update Category Counts dynamically
async function updateCategoryCounts() {
  try {
    const response = await fetch(`${API_BASE_URL}/books`);
    if (!response.ok) return;
    const json = await response.json();
    const allBooks = json.data || [];

    const counts = {
      "Programming": 0,
      "Database": 0,
      "Web Development": 0,
      "AI": 0,
      "Fiction": 0
    };

    allBooks.forEach(book => {
      const cat = book.category;
      if (cat && counts.hasOwnProperty(cat)) {
        counts[cat]++;
      }
    });

    const categoryElements = document.querySelectorAll("[data-category-target]");
    categoryElements.forEach(el => {
      const cat = el.getAttribute("data-category-target");
      const countEl = el.querySelector(".category-card-count");
      if (countEl && counts.hasOwnProperty(cat)) {
        const count = counts[cat];
        countEl.textContent = count === 1 ? "1 Book" : count + " Books";
      }
    });
  } catch (e) {
    // Graceful fallback
  }
}

// Open Book Details Modal
async function openBookDetails(bookId) {
  try {
    const response = await fetch(`${API_BASE_URL}/books/${bookId}`);
    if (!response.ok) throw new Error("Book not found");
    const json = await response.json();
    const book = json.data;

    const modalEl = document.getElementById("bookDetailsModal");
    if (!modalEl || typeof bootstrap === "undefined") return;

    document.getElementById("modalBookTitle").textContent = book.title;
    document.getElementById("modalBookAuthor").textContent = book.author;
    document.getElementById("modalBookCategory").textContent = book.category;
    document.getElementById("modalBookPrice").textContent = formatPrice(book.price);
    document.getElementById("modalBookAvailability").textContent = book.availability ? "Available" : "Not Available";
    document.getElementById("modalBookRating").textContent = (book.rating ? Number(book.rating).toFixed(1) : "4.5") + "/5";
    document.getElementById("modalBookIsbn").textContent = book.isbn || "Not Specified";
    document.getElementById("modalBookDescription").textContent = book.description || "";

    const favBtn = document.getElementById("modalFavoriteBtn");
    if (favBtn) {
      favBtn.setAttribute("data-book-id", book.id);
      updateModalFavoriteButton(book.id);
    }

    const modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
    modalInstance.show();
  } catch (error) {
    console.error("Unable to load book details:", error);
    showToast("Unable to load book details from server.");
  }
}

function updateModalFavoriteButton(bookId) {
  const favBtn = document.getElementById("modalFavoriteBtn");
  if (!favBtn) return;
  const currentModalBookId = Number(favBtn.getAttribute("data-book-id"));
  if (currentModalBookId !== Number(bookId)) return;

  const fav = favoriteBookIds.includes(Number(bookId));
  favBtn.textContent = fav ? "Remove Favorite" : "Add to Favorites";
  if (fav) {
    favBtn.className = "btn btn-secondary";
  } else {
    favBtn.className = "btn btn-outline-secondary";
  }
}

// Surprise Me: Pick a random book from the active list
function triggerSurpriseMe() {
  if (!currentBooks || currentBooks.length === 0) {
    showToast("No books available right now.");
    return;
  }
  const randomIndex = Math.floor(Math.random() * currentBooks.length);
  const randomBook = currentBooks[randomIndex];
  openBookDetails(randomBook.id);
}

// Handle Register Book form submission (POST /api/books to Spring Boot)
async function handleRegisterBook(event) {
  event.preventDefault();

  const form = document.getElementById("registerBookForm");
  if (!form) return;

  if (!form.checkValidity()) {
    form.classList.add("was-validated");
    return;
  }

  const bookData = {
    title: document.getElementById("bookTitleInput").value.trim(),
    author: document.getElementById("bookAuthorInput").value.trim(),
    isbn: document.getElementById("bookIsbnInput").value.trim(),
    category: document.getElementById("bookCategoryInput").value,
    price: parseFloat(document.getElementById("bookPriceInput").value),
    availability: document.getElementById("bookAvailabilityInput").value === "true",
    description: document.getElementById("bookDescriptionInput").value.trim(),
    rating: 4.5
  };

  try {
    const response = await fetch(`${API_BASE_URL}/books`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(bookData)
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(result.message || "Failed to add book");
    }

    // Reset form
    form.reset();
    form.classList.remove("was-validated");

    // Show toast
    showToast("Book added successfully.");

    // Refresh books and category counts from MySQL
    await loadBooks();

    // Scroll to collection
    const collectionSection = document.getElementById("books");
    if (collectionSection) {
      collectionSection.scrollIntoView({ behavior: "smooth" });
    }
  } catch (error) {
    console.error("Error creating book:", error);
    showToast("Error: " + error.message);
  }
}

// Category filter selection handler
function setCategory(categoryName) {
  currentCategory = categoryName;

  // Update filter buttons
  const filterBtns = document.querySelectorAll(".filter-btn");
  filterBtns.forEach(btn => {
    if (btn.getAttribute("data-category") === categoryName) {
      btn.classList.add("active");
    } else {
      btn.classList.remove("active");
    }
  });

  // Update category cards
  const categoryCards = document.querySelectorAll(".category-card");
  categoryCards.forEach(card => {
    if (card.getAttribute("data-category-target") === categoryName) {
      card.classList.add("active");
    } else {
      card.classList.remove("active");
    }
  });

  loadBooks();
}

// Utility: HTML escape
function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

// Setup on DOM Ready
document.addEventListener("DOMContentLoaded", () => {
  loadBooks();
  loadFavorites();

  // Category filter buttons
  const filterButtons = document.querySelectorAll(".filter-btn");
  filterButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      const cat = btn.getAttribute("data-category");
      setCategory(cat);
    });
  });

  // Category section cards
  const categoryCards = document.querySelectorAll(".category-card");
  categoryCards.forEach(card => {
    const handleCategorySelect = () => {
      const targetCategory = card.getAttribute("data-category-target");
      if (targetCategory) {
        setCategory(targetCategory);
        const bookSection = document.getElementById("books");
        if (bookSection) {
          bookSection.scrollIntoView({ behavior: "smooth" });
        }
      }
    };

    card.addEventListener("click", handleCategorySelect);
    card.addEventListener("keydown", (e) => {
      if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        handleCategorySelect();
      }
    });
  });

  // Sorting dropdown
  const sortSelect = document.getElementById("sortSelect");
  if (sortSelect) {
    sortSelect.addEventListener("change", (e) => {
      currentSort = e.target.value;
      loadBooks();
    });
  }

  // Surprise Me button
  const surpriseBtn = document.getElementById("surpriseMeBtn");
  if (surpriseBtn) {
    surpriseBtn.addEventListener("click", triggerSurpriseMe);
  }

  // Register book form
  const registerForm = document.getElementById("registerBookForm");
  if (registerForm) {
    registerForm.addEventListener("submit", handleRegisterBook);
  }

  // Modal favorite button
  const modalFavBtn = document.getElementById("modalFavoriteBtn");
  if (modalFavBtn) {
    modalFavBtn.addEventListener("click", () => {
      const bookId = modalFavBtn.getAttribute("data-book-id");
      if (bookId) {
        toggleFavorite(bookId);
      }
    });
  }

  // Auto-close navbar on mobile when a nav-link is clicked
  const navLinks = document.querySelectorAll(".navbar-nav .nav-link");
  const navCollapse = document.getElementById("mainNavbarNav");
  if (navCollapse && typeof bootstrap !== "undefined") {
    navLinks.forEach(link => {
      link.addEventListener("click", () => {
        if (navCollapse.classList.contains("show")) {
          const bsCollapse = bootstrap.Collapse.getInstance(navCollapse);
          if (bsCollapse) {
            bsCollapse.hide();
          }
        }
      });
    });
  }
});
