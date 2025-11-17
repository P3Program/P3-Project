document.addEventListener('DOMContentLoaded', () => {
    let activeFilter = null;

    const filterButtons = document.querySelectorAll('.filter-button');

    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            const priority = button.dataset.priority;

            // Toggle filter
            if (activeFilter === priority) {
                // Deactivate filter - show all
                activeFilter = null;
                button.classList.remove('active');
                showAllProjects();
            } else {
                // Activate new filter
                activeFilter = priority;

                // Update button states
                filterButtons.forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');

                // Filter projects
                filterProjects(priority);
            }
        });
    });

    function filterProjects(priority) {
        // Get all project boxes from all containers
        const projectBoxes = document.querySelectorAll('.project-box');

        projectBoxes.forEach(box => {
            const priorityClass = `priority-${priority}`;
            if (box.classList.contains(priorityClass)) {
                box.classList.remove('hidden');
            } else {
                box.classList.add('hidden');
            }
        });
    }

    function showAllProjects() {
        const projectBoxes = document.querySelectorAll('.project-box');
        projectBoxes.forEach(box => {
            box.classList.remove('hidden');
        });
    }
});