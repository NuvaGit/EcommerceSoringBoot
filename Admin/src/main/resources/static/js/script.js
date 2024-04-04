const navi = document.querySelector("[data-navbar]");
const navilinks = document.querySelectorAll("[data-nav-link]");
const navitoggle = document.querySelector("[data-nav-toggler]");

navitoggle.addEventListener("click", function () {
  navi.classList.toggle("active");
  this.classList.toggle("active");
});

for (let i = 0; i < navilinks.length; i++) {
  navilinks[i].addEventListener("click", function () {
    navi.classList.remove("active");
    navitoggle.classList.remove("active");
  });
}

const header = document.querySelector("[data-header]");
const toheader = document.querySelector("[data-back-top-btn]");

window.addEventListener("scroll", function () {
  if (window.scrollY >= 200) {
    header.classList.add("active");
    toheader.classList.add("active");
  } else {
    header.classList.remove("active");
    toheader.classList.remove("active");
  }
});

function toggleTheme() {
  var body = document.body;
  body.classList.toggle("scifi");
  body.classList.toggle("modern");
}

function duplicateTable() {
  var table = document.getElementById("mt");
  
  var cloner = table.cloneNode(true);
  
  table.parentNode.insertBefore(cloner, table.nextSibling);
}