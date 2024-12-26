function fetchICPInfo() {
    fetch('/-/api/settings')
        .then(response => response.json())
        .then(data => {
            const icpText = document.getElementById('icp-text');
            if (data.settings && data.settings.ICPLicense) {
                icpText.innerHTML = `<a id="footer-icp" href="https://beian.miit.gov.cn" target="_blank">${data.settings.ICPLicense}</a>`;
            } else {
                icpText.innerHTML = '';
            }
        })
        .catch(error => {
            console.error('Error fetching ICP data:', error);
        });
}

document.addEventListener('DOMContentLoaded', function () {
    fetchICPInfo();
});
