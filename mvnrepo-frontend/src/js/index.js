const protocol = window.location.protocol;
const hostname = window.location.host;
const baseUrl = `${protocol}//${hostname}`;
const releasesUrl = `${baseUrl}/releases`;
const snapshotsUrl = `${baseUrl}/snapshots`;
const privateUrl = `${baseUrl}/private`;
document.getElementById("releases-repo").textContent = releasesUrl;
document.getElementById("snapshots-repo").textContent = snapshotsUrl;
document.getElementById("private-repo").textContent = privateUrl;
Prism.highlightAll();
new ClipboardJS('.copy-btn');

document.getElementById('loginForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/-/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password,
            }),
        });

        if (!response.ok) {
            throw new Error('登录失败');
        }

        const data = await response.json();
        if (data.access_token) {
            localStorage.setItem('access_token', data.access_token);
            alert('登录成功！');
            const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
            modal.hide();
        } else {
            alert('登录失败，未获得有效token');
        }
    } catch (error) {
        alert(error.message);
    }
});