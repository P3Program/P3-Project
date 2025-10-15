document.addEventListener('DOMContentLoaded', () => {

    const addTaskBtn = document.getElementById('addtask');
    const formModal = document.getElementById('task-form-modal');
    const closeFormBtn = document.getElementById('close-form');
    const submitTaskBtn = document.getElementById('submit-task');
    const container = document.getElementById('task-container');
    let currentTaskElement = null;

    loadTasks();

    addTaskBtn.addEventListener('click', () => {
        formModal.style.display = 'flex';
    });


    closeFormBtn.addEventListener('click', () => {
        formModal.style.display = 'none';
    });


    submitTaskBtn.addEventListener('click', () => {
        const title = document.getElementById('task-title').value.trim();
        const name = document.getElementById('task-name').value.trim();
        const date = document.getElementById('task-date').value;
        const caldera = document.getElementById('task-caldera').value;
        const warranty = document.getElementById('task-warranty').value;
        const ssn = document.getElementById('task-ssn').value.trim();
        const phone = document.getElementById('task-phone').value.trim();
        const address = document.getElementById('task-address').value.trim();
        const email = document.getElementById('task-email').value.trim();
        const estTime = document.getElementById('task-est-time').value.trim();
        const dueDate = document.getElementById('task-due-date').value;
        const desc = document.getElementById('task-desc').value.trim();

        if (!title) {
            alert("Task title is required");
            return;
        }


        if (submitTaskBtn.dataset.editMode === 'true' && currentTaskElement) {
            currentTaskElement.innerHTML = `
            <div style="font-weight: bold;">${title}</div>
            <div style="font-size: 0.9em;">Customer: ${name}</div>
            <div style="font-size: 0.9em;">Created: ${date}</div>
            <div style="font-size: 0.9em;">Addr: ${address}</div>
        `;

            currentTaskElement.dataset.title = title;
            currentTaskElement.dataset.name = name;
            currentTaskElement.dataset.date = date;
            currentTaskElement.dataset.caldera = caldera;
            currentTaskElement.dataset.warranty = warranty;
            currentTaskElement.dataset.ssn = ssn;
            currentTaskElement.dataset.phone = phone;
            currentTaskElement.dataset.address = address;
            currentTaskElement.dataset.email = email;
            currentTaskElement.dataset.estTime = estTime;
            currentTaskElement.dataset.dueDate = dueDate;
            currentTaskElement.dataset.desc = desc;

            submitTaskBtn.dataset.editMode = 'false';
            currentTaskElement = null;
        } else {

            const newTask = document.createElement('div');
            newTask.className = 'task';
            newTask.innerHTML = `
            <div style="font-weight: bold;">${title}</div>
            <div style="font-size: 0.9em;">Customer: ${name}</div>
            <div style="font-size: 0.9em;">Created: ${date}</div>
            <div style="font-size: 0.9em;">Addr: ${address}</div>
        `;

            newTask.dataset.title = title;
            newTask.dataset.name = name;
            newTask.dataset.date = date;
            newTask.dataset.caldera = caldera;
            newTask.dataset.warranty = warranty;
            newTask.dataset.ssn = ssn;
            newTask.dataset.phone = phone;
            newTask.dataset.address = address;
            newTask.dataset.email = email;
            newTask.dataset.estTime = estTime;
            newTask.dataset.dueDate = dueDate;
            newTask.dataset.desc = desc;

            newTask.addEventListener('click', () => {
                showTaskDetails(newTask);
            });

            container.appendChild(newTask);
        }

        document.getElementById('task-title').value = '';
        document.getElementById('task-name').value = '';
        document.getElementById('task-date').value = '';
        document.getElementById('task-caldera').value = 'yes';
        document.getElementById('task-warranty').value = 'yes';
        document.getElementById('task-ssn').value = '';
        document.getElementById('task-phone').value = '';
        document.getElementById('task-address').value = '';
        document.getElementById('task-email').value = '';
        document.getElementById('task-est-time').value = '';
        document.getElementById('task-due-date').value = '';
        document.getElementById('task-desc').value = '';

        saveTasks();
        formModal.style.display = 'none';
    });


    function showTaskDetails(taskElement) {
        currentTaskElement = taskElement;
        const detailModal = document.getElementById('task-detail-modal');

        document.getElementById('detail-title').textContent = taskElement.dataset.title;
        document.getElementById('detail-name').textContent = taskElement.dataset.name;
        document.getElementById('detail-date').textContent = taskElement.dataset.date;
        document.getElementById('detail-caldera').textContent = taskElement.dataset.caldera;
        document.getElementById('detail-warranty').textContent = taskElement.dataset.warranty;
        document.getElementById('detail-ssn').textContent = taskElement.dataset.ssn;
        document.getElementById('detail-phone').textContent = taskElement.dataset.phone;
        document.getElementById('detail-address').textContent = taskElement.dataset.address;
        document.getElementById('detail-email').textContent = taskElement.dataset.email;
        document.getElementById('detail-est-time').textContent = taskElement.dataset.estTime;
        document.getElementById('detail-due-date').textContent = taskElement.dataset.dueDate;
        document.getElementById('detail-desc').textContent = taskElement.dataset.desc;

        detailModal.style.display = 'flex';
    }

    const closeDetailBtn = document.getElementById('close-detail');
    closeDetailBtn.addEventListener('click', () => {
        document.getElementById('task-detail-modal').style.display = 'none';
    });

    function saveTasks() {
        const tasks = [];
        document.querySelectorAll('.task').forEach(taskElement => {
            tasks.push({
                title: taskElement.dataset.title,
                name: taskElement.dataset.name,
                date: taskElement.dataset.date,
                caldera: taskElement.dataset.caldera,
                warranty: taskElement.dataset.warranty,
                ssn: taskElement.dataset.ssn,
                phone: taskElement.dataset.phone,
                address: taskElement.dataset.address,
                email: taskElement.dataset.email,
                estTime: taskElement.dataset.estTime,
                dueDate: taskElement.dataset.dueDate,
                desc: taskElement.dataset.desc,
                container: taskElement.parentElement.id
            });
        });
        localStorage.setItem('tasks', JSON.stringify(tasks));
    }

    function loadTasks() {
        const savedTasks = localStorage.getItem('tasks');
        if (savedTasks) {
            const tasks = JSON.parse(savedTasks);
            tasks.forEach(taskData => {
                createTaskElement(taskData);
            });
        }
    }

    function createTaskElement(taskData) {
        const newTask = document.createElement('div');
        newTask.className = 'task';
        newTask.innerHTML = `
        <div style="font-weight: bold;">${taskData.title}</div>
        <div style="font-size: 0.9em;">Customer: ${taskData.name}</div>
        <div style="font-size: 0.9em;">Date: ${taskData.date}</div>
        <div style="font-size: 0.9em;">Addr: ${taskData.address}</div>
    `;

        Object.assign(newTask.dataset, taskData);
        newTask.addEventListener('click', () => showTaskDetails(newTask));


        const targetContainer = document.getElementById(taskData.container) || document.getElementById('task-container');

        targetContainer.appendChild(newTask);
    }

    const editTaskBtn = document.getElementById('edit-task');
    editTaskBtn.addEventListener('click', () => {
        if (currentTaskElement) {
            document.getElementById('task-title').value = currentTaskElement.dataset.title;
            document.getElementById('task-name').value = currentTaskElement.dataset.name;
            document.getElementById('task-date').value = currentTaskElement.dataset.date;
            document.getElementById('task-caldera').value = currentTaskElement.dataset.caldera;
            document.getElementById('task-warranty').value = currentTaskElement.dataset.warranty;
            document.getElementById('task-ssn').value = currentTaskElement.dataset.ssn;
            document.getElementById('task-phone').value = currentTaskElement.dataset.phone;
            document.getElementById('task-address').value = currentTaskElement.dataset.address;
            document.getElementById('task-email').value = currentTaskElement.dataset.email;
            document.getElementById('task-est-time').value = currentTaskElement.dataset.estTime;
            document.getElementById('task-due-date').value = currentTaskElement.dataset.dueDate;
            document.getElementById('task-desc').value = currentTaskElement.dataset.desc;

            submitTaskBtn.dataset.editMode = 'true';

            document.getElementById('task-detail-modal').style.display = 'none';
            formModal.style.display = 'flex';
        }
    });

    const deleteTaskBtn = document.getElementById('delete-task');
    deleteTaskBtn.addEventListener('click', () => {

        const confirmDelete = confirm("Are you sure you want to delete this task?");

        if (confirmDelete && currentTaskElement) {
            currentTaskElement.remove();
            saveTasks();
            document.getElementById('task-detail-modal').style.display = 'none';
            console.log("Task deleted");
        }
    });

    const moveToReviewBtn = document.getElementById('move-to-review');
    moveToReviewBtn.addEventListener('click', () => {
        if (currentTaskElement) {
            const reviewContainer = document.getElementById('review-container');
            reviewContainer.appendChild(currentTaskElement);

            saveTasks();
            document.getElementById('task-detail-modal').style.display = 'none';
        }
    });


});