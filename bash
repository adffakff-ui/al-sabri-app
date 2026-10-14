git clone git@github.com:adffakff-ui/al-sabri-app.git
cd al-sabri-app
git checkout -b fix/pwa-and-apk
git add index.html sw.js manifest.json .github/workflows/pages.yml
git commit -m "fix(web): add PWA (sw, manifest), point APK to release, add Pages workflow"
git push origin fix/pwa-and-apk
