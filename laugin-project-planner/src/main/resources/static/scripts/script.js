document.addEventListener('DOMContentLoaded', () => {

    const addTaskBtn = document.getElementById('addtask');
    const formModal = document.getElementById('task-form-modal');
    const closeFormBtn = document.getElementById('close-form');

    addTaskBtn.addEventListener('click', () => {
        formModal.style.display = 'flex';
        showPage(1);
        updatePriorityImmediately();
        currentPage = 1;
    });

    closeFormBtn.addEventListener('click', () => {
        formModal.style.display = 'none';
    });

    document.querySelectorAll('input[name="caldera"], input[name="warranty"]').forEach(radio => {
        radio.addEventListener('change', updatePriorityImmediately);
    });

    function updatePriorityImmediately() {
        const calderaChecked = document.querySelector('input[name="caldera"]:checked');
        const warrantyChecked = document.querySelector('input[name="warranty"]:checked');
        if (calderaChecked && warrantyChecked) {
            const priority = calculatePriority();
            document.querySelector('select[name="priority"]').value = priority;
        }
    }

    function calculatePriority() {
        const caldera = document.querySelector('input[name="caldera"]:checked').value === "1";
        const warranty = document.querySelector('input[name="warranty"]:checked').value === "1";
        if (caldera && warranty) {
            return "Red";
        } else if (caldera && !warranty) {
            return "Yellow";
        } else {
            return "Green";
        }
    }

    let currentPage = 1;

    document.getElementById('next-btn').addEventListener('click', () => {
        if (currentPage < 3) {
            currentPage++;
            showPage(currentPage);
        }
    });

    document.getElementById('prev-btn').addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
        }
    });

    function showPage(pageNum) {
        document.querySelectorAll('.form-page').forEach(page => page.classList.add('hidden'));
        document.getElementById(`page-${pageNum}`).classList.remove('hidden');

        document.querySelectorAll('.step').forEach((step, index) => {
            step.className = 'step';
            if (index + 1 < pageNum) step.classList.add('completed');
            else if (index + 1 === pageNum) step.classList.add('active');
        });

        document.getElementById('prev-btn').style.display = pageNum === 1 ? 'none' : 'block';
        document.getElementById('next-btn').style.display = pageNum === 3 ? 'none' : 'block';
        document.getElementById('submit-btn').style.display = pageNum === 3 ? 'block' : 'none';
    }

    const containers = {
        review:   { el: document.getElementById('review-container'),  status: 'underReview' },
        progress: { el: document.getElementById('progress-container'),status: 'inProgress' },
        billing:  { el: document.getElementById('billing-container'), status: 'billing' }
    };

    document.querySelectorAll('#review-container .project-box, #progress-container .project-box, #billing-container .project-box').forEach(makeDraggable);

    function makeDraggable(card) {
        card.setAttribute('draggable', 'true');
        card.addEventListener('dragstart', onDragStart);
        card.addEventListener('dragend', onDragEnd);
    }

    let dragged = null;

    function onDragStart(e) {
        dragged = e.currentTarget;
        dragged.classList.add('dragging');
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('text/plain', dragged.dataset.id || '');
    }

    function onDragEnd() {
        if (dragged) dragged.classList.remove('dragging');
        dragged = null;
    }

    Object.values(containers).forEach(({ el }) => {
        el.addEventListener('dragover', (e) => {
            e.preventDefault();
            const afterEl = getAfterElement(el, e.clientY);
            if (!dragged) return;
            if (afterEl == null) el.appendChild(dragged);
            else el.insertBefore(dragged, afterEl);
        });

        el.addEventListener('dragenter', () => el.classList.add('drag-over'));
        el.addEventListener('dragleave', () => el.classList.remove('drag-over'));

        el.addEventListener('drop', async (e) => {
            e.preventDefault();
            el.classList.remove('drag-over');
            if (!dragged) return;

            const toStatus = getStatusByContainer(el);
            const fromStatus = dragged.dataset.status;
            const id = dragged.dataset.id;

            dragged.dataset.status = toStatus;

            if (toStatus !== fromStatus) {
                try {
                    await fetch(`/api/projects/${id}/move?toStatus=${encodeURIComponent(toStatus)}`, {
                        method: 'PATCH'
                    });
                } catch (err) {
                    console.error('Kunne ikke flytte projekt', err);
                }
            }

            const orderedIds = [...el.querySelectorAll('.project-box')].map(c => Number(c.dataset.id));
            try {
                await fetch(`/api/projects/reorder?status=${encodeURIComponent(toStatus)}`, {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(orderedIds)
                });
            } catch (err) {
                console.error('Kunne ikke gemme rækkefølge', err);
            }
        });
    });

    function getAfterElement(container, y) {
        const els = [...container.querySelectorAll('.project-box:not(.dragging)')];
        let closest = { offset: Number.NEGATIVE_INFINITY, el: null };
        for (const el of els) {
            const box = el.getBoundingClientRect();
            const offset = y - box.top - box.height / 2;
            if (offset < 0 && offset > closest.offset) closest = { offset, el };
        }
        return closest.el;
    }

    function getStatusByContainer(el) {
        if (el === containers.task.el) return containers.task.status;
        if (el === containers.review.el) return containers.review.status;
        if (el === containers.progress.el) return containers.progress.status;
        if (el === containers.billing.el) return containers.billing.status;
        return 'allProjects';
    }
});
