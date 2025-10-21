// Animate numbers counting up
function animateValue(element, start, end, duration) {
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        const value = Math.floor(progress * (end - start) + start);
        element.textContent = value;
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}

// Calculate and display statistics
window.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        const redCount = parseInt(document.querySelector('.red .kpi-value').textContent) || 0;
        const yellowCount = parseInt(document.querySelector('.yellow .kpi-value').textContent) || 0;
        const greenCount = parseInt(document.querySelector('.green .kpi-value').textContent) || 0;

        const total = redCount + yellowCount + greenCount;

        // Animate total tasks
        const totalElement = document.getElementById('totalTasks');
        animateValue(totalElement, 0, total, 1500);

        // Animate completion rate
        const rateElement = document.getElementById('completionRate');
        animateValue(rateElement, 0, completionRate, 1500);
        rateElement.textContent = completionRate + '%';

        // Add pulse animation to KPI values
        document.querySelectorAll('.kpi-value').forEach((el, index) => {
            setTimeout(() => {
                el.style.animation = 'countUp 0.6s ease forwards';
            }, 300 + (index * 200));
        });
    }, 500);
});

// Add subtle parallax effect on mouse move
document.addEventListener('mousemove', (e) => {
    const cards = document.querySelectorAll('.task-display > div');
    const x = e.clientX / window.innerWidth - 0.5;
    const y = e.clientY / window.innerHeight - 0.5;

    cards.forEach((card, index) => {
        const intensity = (index + 1) * 5;
        card.style.transform = `translateY(-8px) perspective(1000px) rotateX(${y * intensity}deg) rotateY(${-x * intensity}deg)`;
    });
});

// Reset transform when mouse leaves
document.addEventListener('mouseleave', () => {
    const cards = document.querySelectorAll('.task-display > div');
    cards.forEach(card => {
        card.style.transform = '';
    });
});