document.addEventListener('DOMContentLoaded', () => {
const csrfToken = document.querySelector('input[name="_csrf"]').value;
const csrfHeader = document.querySelector('input[name="_csrf_header"]')?.value || 'X-CSRF-TOKEN';


    const containers = {
        review:   { el: document.getElementById('review-container'),  status: 'underReview' },
        progress: { el: document.getElementById('progress-container'),status: 'inProgress' },
        billing:  { el: document.getElementById('billing-container'), status: 'billing' }
    };

    function initializeDraggableCards() {
        document.querySelectorAll('.project-box').forEach(card => {
            // Only make draggable if not in allProjects
            if (card.dataset.status !== 'allProjects') {
                makeDraggable(card);
            }
        });
    }

    // Call after delay for fragments to render
    setTimeout(initializeDraggableCards, 50);


    function makeDraggable(card) {
        card.setAttribute('draggable', 'true');

        // Remove old listeners
        card.removeEventListener('dragstart', onDragStart);
        card.removeEventListener('dragend', onDragEnd);

        // Add new listeners
        card.addEventListener('dragstart', onDragStart);
        card.addEventListener('dragend', onDragEnd);
    }

    let dragged = null;
    let draggedWrapper = null;

    function onDragStart(e) {
        dragged = e.currentTarget;
        draggedWrapper = dragged.parentElement;
        draggedWrapper.classList.add('dragging');
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('text/plain', dragged.dataset.id || '');
    }

    function onDragEnd() {
        if (draggedWrapper) draggedWrapper.classList.remove('dragging');
        dragged = null;
        draggedWrapper = null;
    }

    Object.values(containers).forEach(({ el }) => {
        el.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';

            if (!draggedWrapper) return;

            const afterEl = getAfterElement(el, e.clientY);

            try {
                // Only insert if it's actually a different position
                if (afterEl == null) {
                    if (draggedWrapper !== el.lastElementChild) {
                        el.appendChild(draggedWrapper);
                    }
                } else {
                    if (draggedWrapper !== afterEl && draggedWrapper.nextElementSibling !== afterEl) {
                        el.insertBefore(draggedWrapper, afterEl);
                    }
                }
            } catch (error) {
                console.error('Error moving element:', error);
            }
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

            //makeDraggable(dragged);

            if (toStatus !== fromStatus) {
                try {
                    await fetch(`/api/projects/${id}/move?toStatus=${encodeURIComponent(toStatus)}`, {
                        method: 'PATCH',
                        credentials: 'include', // keep cookies
                        headers: {
                            'Content-Type': 'application/json',
                            [csrfHeader]: csrfToken,
                        },
                    });
                } catch (err) {
                    console.error('Kunne ikke flytte projekt', err);
                }
            }

            const orderedIds = [...el.querySelectorAll('.project-box')].map(c => Number(c.dataset.id));
            try {
                await fetch(`/api/projects/reorder?status=${encodeURIComponent(toStatus)}`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken,
                    },
                    body: JSON.stringify(orderedIds)
                });
            } catch (err) {
                console.error('Kunne ikke gemme rækkefølge', err);
            }
        });
    });

    function getAfterElement(container, y) {
        const els = [...container.children].filter(child =>
            !child.classList.contains('dragging')
        );

        let closest = { offset: Number.NEGATIVE_INFINITY, el: null };
        for (const el of els) {
            const box = el.getBoundingClientRect();
            const offset = y - box.top - box.height / 2;
            if (offset < 0 && offset > closest.offset) closest = { offset, el };
        }
        return closest.el;
    }

    function getStatusByContainer(el) {
        if (el === containers.review.el) return containers.review.status;
        if (el === containers.progress.el) return containers.progress.status;
        if (el === containers.billing.el) return containers.billing.status;
        return 'allProjects';
    }
});
