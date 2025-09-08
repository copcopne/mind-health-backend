document.addEventListener("DOMContentLoaded", function () {
    const navLinks = document.querySelectorAll("#sidebarMenu .list-group-item");

    navLinks.forEach(link => {
        // so sánh link hiện tại với href của <a>
        if (link.getAttribute("href") === window.location.pathname) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });
});