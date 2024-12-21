let host = window.location.host
let protocol = window.location.protocol

document.getElementById("maven-repo-releases")
    .textContent = `maven("${protocol}://${host}/releases")`

document.getElementById("maven-repo-snapshots")
    .textContent = `maven("${protocol}://${host}/snapshots")`

document.getElementById("maven-repo-private")
    .textContent = `maven("${protocol}://${host}/private")`