# Build StreamBox APK without a PC

This project includes a GitHub Actions cloud-build workflow. GitHub's servers compile the APK; you only need an Android phone and a GitHub account.

1. In your phone browser, sign in to https://github.com and create a **New repository** named `StreamBox` (a public or private repository is fine).
2. Upload the **contents** of this project folder to the repository root (not the ZIP file itself). Make sure `.github/workflows/build-apk.yml` is included. If the GitHub mobile website cannot upload a folder, use GitHub's web editor to create the files, or use a Git client app that supports uploading a folder.
3. Open the repository's **Actions** tab, choose **Build StreamBox APK**, and tap **Run workflow** if it has not started automatically after upload.
4. Wait for the workflow to finish successfully. Open that run, scroll to **Artifacts**, and download `StreamBox-debug-APK`.
5. Extract the downloaded artifact ZIP and tap `app-debug.apk` to install. Android may ask you to allow installs from your browser or file manager.

The APK is a debug build for testing. No IPTV service or playlist is included; add an M3U/M3U8 playlist you are authorized to use.
