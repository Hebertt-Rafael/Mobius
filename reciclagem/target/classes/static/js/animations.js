/* =========================================================
   Mobius - animations.js
   Automatic dynamic features for the home page:
   1. Scroll-reveal (sections & cards fade/slide in)
   2. Animated number counters (triggered on scroll)
   3. Rotating tip messages (hero area)
   4. Floating leaf particles background
   5. Typing effect on hero h1
   ========================================================= */

(function () {
  "use strict";

  /* -------------------------------------------------------
     1. SCROLL REVEAL
     Sections with .reveal-section fade up when entering view.
     Cards with .reveal-card stagger with --delay CSS variable.
  ------------------------------------------------------- */
  const revealSections = document.querySelectorAll(".reveal-section");
  const revealCards    = document.querySelectorAll(".reveal-card");

  // Initial hidden state injected via JS (not CSS) so it degrades gracefully
  revealSections.forEach((el) => {
    el.style.opacity   = "0";
    el.style.transform = "translateY(40px)";
    el.style.transition = "opacity 0.7s ease, transform 0.7s ease";
  });

  revealCards.forEach((el) => {
    const delay = el.style.getPropertyValue("--delay") || "0ms";
    el.style.opacity   = "0";
    el.style.transform = "translateY(30px) scale(0.97)";
    el.style.transition = `opacity 0.6s ease ${delay}, transform 0.6s ease ${delay}`;
  });

  function revealOnScroll(entries, observer) {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;
      entry.target.style.opacity   = "1";
      entry.target.style.transform = "translateY(0) scale(1)";
      observer.unobserve(entry.target);
    });
  }

  const sectionObserver = new IntersectionObserver(revealOnScroll, {
    threshold: 0.1,
    rootMargin: "0px 0px -40px 0px",
  });

  revealSections.forEach((el) => sectionObserver.observe(el));
  revealCards.forEach((el) => sectionObserver.observe(el));


  /* -------------------------------------------------------
     2. ANIMATED COUNTERS
     Elements with .counter and data-target="N" count up
     when they scroll into view. Runs once.
  ------------------------------------------------------- */
  const counters = document.querySelectorAll(".counter");

  function animateCounter(el) {
    const target   = parseInt(el.dataset.target, 10);
    const prefix   = el.dataset.prefix  || "";
    const suffix   = el.dataset.suffix  || "";
    const duration = 1600; // ms
    const start    = performance.now();

    function step(now) {
      const elapsed  = now - start;
      const progress = Math.min(elapsed / duration, 1);
      // Ease-out cubic
      const eased    = 1 - Math.pow(1 - progress, 3);
      const current  = Math.round(eased * target);
      el.textContent = prefix + current + suffix;
      if (progress < 1) requestAnimationFrame(step);
    }

    requestAnimationFrame(step);
  }

  const counterObserver = new IntersectionObserver(
    (entries, obs) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        animateCounter(entry.target);
        obs.unobserve(entry.target);
      });
    },
    { threshold: 0.5 }
  );

  counters.forEach((el) => counterObserver.observe(el));


  /* -------------------------------------------------------
     3. ROTATING ECO-TIPS BELOW HERO SUBTITLE
     A small pill that cycles through recycling tips every 3.5s.
  ------------------------------------------------------- */
  const tips = [
    "💡 Vidro pode ser reciclado infinitas vezes sem perder qualidade!",
    "♻️ Papel reciclado economiza 60% de água na produção.",
    "🌿 1 tonelada de plástico reciclado poupa 2 barris de petróleo.",
    "🔋 Pilhas jogadas no lixo podem contaminar até 400 m² de solo.",
    "🐦 A reciclagem reduz emissões de CO₂ e protege os rios.",
    "🌎 Brasil gera 80 mi de toneladas de resíduos por ano — separe o seu!",
  ];

  const heroText = document.querySelector(".hero-text");
  if (heroText) {
    const tipPill = document.createElement("div");
    tipPill.id = "eco-tip-rotator";
    tipPill.style.cssText = `
      display: inline-flex;
      align-items: center;
      gap: 8px;
      margin-top: 10px;
      padding: 8px 16px;
      background: rgba(88,129,87,0.12);
      border: 1px solid rgba(88,129,87,0.3);
      border-radius: 999px;
      font-size: 0.88rem;
      color: #2f4734;
      font-weight: 600;
      min-height: 38px;
      transition: opacity 0.45s ease;
      max-width: 520px;
    `;
    tipPill.textContent = tips[0];
    heroText.appendChild(tipPill);

    let tipIndex = 0;
    setInterval(() => {
      tipPill.style.opacity = "0";
      setTimeout(() => {
        tipIndex = (tipIndex + 1) % tips.length;
        tipPill.textContent = tips[tipIndex];
        tipPill.style.opacity = "1";
      }, 450);
    }, 3500);
  }


  /* -------------------------------------------------------
     4. FLOATING LEAF PARTICLES
     Generates soft leaf/circle particles drifting upward
     in the background of <main>.
  ------------------------------------------------------- */
  const mainEl = document.querySelector("main");
  if (mainEl) {
    const canvas = document.createElement("canvas");
    canvas.id = "particles-canvas";
    canvas.style.cssText = `
      position: fixed;
      top: 0; left: 0;
      width: 100%; height: 100%;
      pointer-events: none;
      z-index: 0;
      opacity: 0.35;
    `;
    document.body.insertBefore(canvas, document.body.firstChild);

    const ctx = canvas.getContext("2d");
    const PARTICLE_COUNT = 22;
    const COLORS = ["#a3b18a", "#588157", "#dad7cd", "#b7c9a8", "#3a5a40"];

    let particles = [];

    function resizeCanvas() {
      canvas.width  = window.innerWidth;
      canvas.height = window.innerHeight;
    }
    resizeCanvas();
    window.addEventListener("resize", resizeCanvas, { passive: true });

    function randomBetween(a, b) {
      return a + Math.random() * (b - a);
    }

    function createParticle() {
      return {
        x:       randomBetween(0, canvas.width),
        y:       randomBetween(canvas.height * 0.2, canvas.height * 1.1),
        r:       randomBetween(4, 14),
        color:   COLORS[Math.floor(Math.random() * COLORS.length)],
        speedY:  randomBetween(0.18, 0.55),
        speedX:  randomBetween(-0.18, 0.18),
        opacity: randomBetween(0.25, 0.75),
        wobble:  randomBetween(0, Math.PI * 2),
        wobbleSpeed: randomBetween(0.008, 0.025),
      };
    }

    for (let i = 0; i < PARTICLE_COUNT; i++) {
      particles.push(createParticle());
    }

    function drawLeaf(x, y, r, color, opacity) {
      ctx.save();
      ctx.globalAlpha = opacity;
      ctx.fillStyle   = color;
      ctx.beginPath();
      // Simple oval leaf shape
      ctx.ellipse(x, y, r, r * 1.6, Math.PI / 4, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }

    function animateParticles() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      particles.forEach((p) => {
        p.wobble += p.wobbleSpeed;
        p.x += p.speedX + Math.sin(p.wobble) * 0.3;
        p.y -= p.speedY;

        drawLeaf(p.x, p.y, p.r, p.color, p.opacity);

        // Reset when particle floats off screen top
        if (p.y < -p.r * 2) {
          p.y = canvas.height + p.r * 2;
          p.x = randomBetween(0, canvas.width);
        }
      });

      requestAnimationFrame(animateParticles);
    }

    animateParticles();
  }


  /* -------------------------------------------------------
     5. TYPING EFFECT on Hero H1
     Erases existing text and types it character by character.
  ------------------------------------------------------- */
  const heroH1 = document.querySelector(".hero-text h1");
  if (heroH1) {
    const originalText = heroH1.textContent.trim();
    heroH1.textContent = "";

    // Blinking cursor
    const cursor = document.createElement("span");
    cursor.textContent = "|";
    cursor.style.cssText = `
      display: inline-block;
      color: #588157;
      animation: cursorBlink 0.85s step-end infinite;
      margin-left: 2px;
      font-weight: 300;
    `;
    heroH1.appendChild(cursor);

    // Inject cursor blink keyframe
    if (!document.getElementById("cursor-style")) {
      const style = document.createElement("style");
      style.id = "cursor-style";
      style.textContent = `
        @keyframes cursorBlink {
          0%, 100% { opacity: 1; }
          50%       { opacity: 0; }
        }
      `;
      document.head.appendChild(style);
    }

    let charIndex = 0;
    const typingDelay = 48; // ms per character

    function typeNextChar() {
      if (charIndex < originalText.length) {
        heroH1.insertBefore(
          document.createTextNode(originalText[charIndex]),
          cursor
        );
        charIndex++;
        setTimeout(typeNextChar, typingDelay);
      } else {
        // Remove cursor after typing finishes (optional - keep for blinking)
        // cursor.remove();
      }
    }

    // Start typing after a short delay so the page loads first
    setTimeout(typeNextChar, 420);
  }

})();
