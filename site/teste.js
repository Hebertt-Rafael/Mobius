class MobileNavbar {
  constructor(mobileMenu, navList, navLinks) {
    this.mobileMenu = document.querySelector(mobileMenu);
    this.navList = document.querySelector(navList);
    this.navLinks = document.querySelectorAll(navLinks);
    this.activeClass = "active";

    this.handleClick = this.handleClick.bind(this);
  }

  animateLinks() {
    this.navLinks.forEach((link, index) => {
      link.style.animation
        ? (link.style.animation = "")
        : (link.style.animation = `navLinkFade 0.5s ease forwards ${
            index / 7 + 0.3
          }s`);
    });
  }

  handleClick() {
    this.navList.classList.toggle(this.activeClass);
    this.mobileMenu.classList.toggle(this.activeClass);
    this.animateLinks();
  }

  addClickEvent() {
    this.mobileMenu.addEventListener("click", this.handleClick);
  }

  init() {
    if (this.mobileMenu) {
      this.addClickEvent();
    }
    return this;
  }
}

const mobileNavbar = new MobileNavbar(
  ".mobile-menu",
  ".nav-list",
  ".nav-list li",
);
mobileNavbar.init();

const carousel = document.querySelector(".carousel");
const carouselTrack = document.querySelector(".carousel-track");
const slides = document.querySelectorAll(".carousel .slide");
const nextButton = document.querySelector(".carousel-btn.next");
const prevButton = document.querySelector(".carousel-btn.prev");
const dotsContainer = document.querySelector(".carousel-dots");
let currentSlideIndex = 0;
let autoPlayTimer = null;

function renderDots() {
  if (!dotsContainer || !slides.length) return;
  dotsContainer.innerHTML = "";
  slides.forEach((_, idx) => {
    const dot = document.createElement("button");
    dot.className = `carousel-dot ${idx === currentSlideIndex ? "active" : ""}`;
    dot.setAttribute("aria-label", `Ir para slide ${idx + 1}`);
    dot.addEventListener("click", () => {
      currentSlideIndex = idx;
      updateSlidePosition();
      restartAutoPlay();
    });
    dotsContainer.appendChild(dot);
  });
}

function updateDots() {
  if (!dotsContainer) return;
  const dots = dotsContainer.querySelectorAll(".carousel-dot");
  dots.forEach((dot, idx) => {
    dot.classList.toggle("active", idx === currentSlideIndex);
  });
}

function updateSlidePosition() {
  if (!carouselTrack) return;
  carouselTrack.style.transform = `translateX(-${currentSlideIndex * 100}%)`;
  updateDots();
}

function goToNextSlide() {
  if (!slides.length) return;
  currentSlideIndex = (currentSlideIndex + 1) % slides.length;
  updateSlidePosition();
}

function goToPrevSlide() {
  if (!slides.length) return;
  currentSlideIndex = (currentSlideIndex - 1 + slides.length) % slides.length;
  updateSlidePosition();
}

function startAutoPlay() {
  if (autoPlayTimer || slides.length <= 1) return;
  autoPlayTimer = setInterval(goToNextSlide, 4500);
}

function restartAutoPlay() {
  if (autoPlayTimer) {
    clearInterval(autoPlayTimer);
    autoPlayTimer = null;
  }
  startAutoPlay();
}

function stopAutoPlay() {
  if (!autoPlayTimer) return;
  clearInterval(autoPlayTimer);
  autoPlayTimer = null;
}

if (carousel && carouselTrack && slides.length > 1) {
  renderDots();

  if (nextButton) {
    nextButton.addEventListener("click", () => {
      goToNextSlide();
      restartAutoPlay();
    });
  }

  if (prevButton) {
    prevButton.addEventListener("click", () => {
      goToPrevSlide();
      restartAutoPlay();
    });
  }

  carousel.addEventListener("mouseenter", stopAutoPlay);
  carousel.addEventListener("mouseleave", startAutoPlay);

  updateSlidePosition();
  startAutoPlay();
}

const binsParallax = document.querySelector(".bins-parallax");

function initBinsScrollReveal() {
  if (!binsParallax) return;

  if (
    !("IntersectionObserver" in window) ||
    !document.documentElement.classList.contains("js-reveal")
  ) {
    binsParallax.classList.add("bins-in-view");
    return;
  }

  const revealNow = () => {
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        binsParallax.classList.add("bins-in-view");
      });
    });
  };

  const observer = new IntersectionObserver(
    (entries, obs) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        revealNow();
        obs.unobserve(entry.target);
      });
    },
    { threshold: 0.06, rootMargin: "0px 0px 8% 0px" },
  );

  observer.observe(binsParallax);
}

function initBinsParallaxMotion() {
  if (!binsParallax) return;

  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  if (reduceMotion) return;

  let ticking = false;

  const updateParallax = () => {
    const rect = binsParallax.getBoundingClientRect();
    const viewportCenter = window.innerHeight / 2;
    const sectionCenter = rect.top + rect.height / 2;
    const distance = sectionCenter - viewportCenter;
    const shift = Math.max(-50, Math.min(50, distance * -0.12));
    binsParallax.style.backgroundPosition = `center calc(50% + ${shift}px)`;
    ticking = false;
  };

  const requestParallaxUpdate = () => {
    if (ticking) return;
    ticking = true;
    window.requestAnimationFrame(updateParallax);
  };

  window.addEventListener("scroll", requestParallaxUpdate, { passive: true });
  window.addEventListener("resize", requestParallaxUpdate);
  requestParallaxUpdate();
}

initBinsScrollReveal();
initBinsParallaxMotion();

function updateNavbarUser() {
  const navLinks = document.querySelectorAll(".nav-list a");
  if (!navLinks.length) return;

  let loggedUser = null;
  try {
    const raw =
      localStorage.getItem("mobius_usuario_logado") ||
      sessionStorage.getItem("mobius_usuario_logado");
    if (raw) loggedUser = JSON.parse(raw);
  } catch (e) {
    loggedUser = null;
  }

  navLinks.forEach((link) => {
    const href = (link.getAttribute("href") || "").toLowerCase();
    if (href === "login.html" || href === "cadastro.html" || link.dataset.authLink === "true") {
      link.dataset.authLink = "true";
      link.setAttribute("href", "login.html");
      if (loggedUser) {
        const firstName = (
          loggedUser.nome ||
          loggedUser.razaoSocial ||
          loggedUser.nomeCompleto ||
          "Minha Conta"
        )
          .toString()
          .trim()
          .split(" ")[0];
        link.innerHTML = `<i class="fa-solid fa-circle-user" aria-hidden="true"></i> Olá, ${firstName}`;
      } else {
        link.textContent = "Entrar";
      }
    }
  });
}

window.updateNavbarUser = updateNavbarUser;
updateNavbarUser();
