document.addEventListener('DOMContentLoaded', () => {
    const toggles = document.querySelectorAll('.faq-toggle');

    toggles.forEach(toggle => {
        toggle.addEventListener('click', () => {
            const faqContent = toggle.parentElement.nextElementSibling;

            // Toggle visibility
            faqContent.classList.toggle('show');
            toggle.textContent = faqContent.classList.contains('show') ? '▲' : '▼';
        });
    });
});
