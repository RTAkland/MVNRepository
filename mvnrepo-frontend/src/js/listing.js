const apiUrl = '/-/api/artifacts';
let currentPath = '';

function getAccessToken() {
    return localStorage.getItem('access_token');
}

function formatFileSize(size) {
    const units = ['B', 'KB', 'MB', 'GB', 'TB'];
    let unitIndex = 0;
    let sizeInUnit = size;

    while (sizeInUnit >= 1024 && unitIndex < units.length - 1) {
        sizeInUnit /= 1024;
        unitIndex++;
    }

    return `${sizeInUnit.toFixed(2)} ${units[unitIndex]}`;
}

function loadArtifacts(path = '') {
    currentPath = path;
    const breadcrumb = document.getElementById('breadcrumb');
    breadcrumb.innerHTML = `当前位置: /${path || ''}`;

    const headers = {};
    const token = getAccessToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    fetch(`${apiUrl}${path ? `/${path}/` : ''}`, { headers })
        .then(response => {
            if (response.status === 401) {
                showLoginModal();
                throw new Error('Unauthorized');
            }
            return response.json();
        })
        .then(data => {
            const artifactList = document.getElementById('artifact-list');
            artifactList.innerHTML = '';
            if (path) {
                const backLink = document.createElement('div');
                backLink.innerHTML = `<span class="back-link" onclick="goBack()">..</span>`;
                artifactList.appendChild(backLink);
            }
            data.data.forEach(item => {
                const div = document.createElement('div');
                if (item.isDirectory) {
                    div.innerHTML = `<span class="folder" onclick="loadArtifacts('${path ? path + '/' + item.name : item.name}')">${item.name}</span>`;
                } else {
                    const fileSize = item.size ? `<span class="file-size">(${formatFileSize(item.size)})</span>` : '';
                    div.innerHTML = `<span class="file" onclick="downloadFile('${path ? path + '/' + item.name : item.name}')">${item.name}</span> ${fileSize}`;
                }
                artifactList.appendChild(div);
            });
        })
        .catch(error => {
            if (error.message !== 'Unauthorized') {
                console.error('Error loading artifacts:', error);
            }
        });
}

function showLoginModal() {
    const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
    loginModal.show();
}

function goBack() {
    const pathParts = currentPath.split('/');
    pathParts.pop();
    const newPath = pathParts.join('/');
    loadArtifacts(newPath);
}

function downloadFile(filePath) {
    window.location.href = `${apiUrl}/${filePath}`;
}

function redirectToLogin() {
    window.location.href = '/';
}

loadArtifacts();