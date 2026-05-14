// Captura screenshots do DevMatch via Puppeteer (Chromium headless).
// Roda dentro de um container ghcr.io/puppeteer/puppeteer:latest com bind-mounts:
//   /scripts  -> ./scripts (este arquivo)
//   /output   -> ./docs/screenshots (destino dos PNGs)
// Pre-requisitos: docker compose up (frontend em :5173, backend em :8080).

const puppeteer = require('puppeteer');

const FRONT = process.env.FRONT_URL || 'http://host.docker.internal:5173';
const API = process.env.API_URL || 'http://host.docker.internal:8080';
const OUT = process.env.OUT_DIR || '/output';
const EMAIL = process.env.LOGIN_EMAIL || 'rafa@example.com';
const PASSWORD = process.env.LOGIN_PASSWORD || 'senha12345';

async function loginAndStoreToken(page) {
  // Sobe na origem do frontend para poder mexer no localStorage dele
  await page.goto(`${FRONT}/login`, { waitUntil: 'networkidle0' });

  const result = await page.evaluate(async (api, email, password) => {
    const r = await fetch(`${api}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!r.ok) {
      throw new Error('Login falhou: HTTP ' + r.status + ' ' + (await r.text()));
    }
    return r.json();
  }, API, EMAIL, PASSWORD);

  await page.evaluate((token, user) => {
    localStorage.setItem('devmatch_token', token);
    localStorage.setItem('devmatch_user', JSON.stringify(user));
  }, result.token, result.user);

  console.log('  + autenticado como ' + result.user.email);
}

async function shoot(page, path, file, settleMs = 800) {
  await page.goto(`${FRONT}${path}`, { waitUntil: 'networkidle0' });
  await new Promise((r) => setTimeout(r, settleMs));
  await page.screenshot({ path: `${OUT}/${file}` });
  console.log('  + ' + file);
}

(async () => {
  console.log('Iniciando Chromium headless...');
  const browser = await puppeteer.launch({
    headless: 'new',
    args: [
      '--no-sandbox',
      '--disable-setuid-sandbox',
      '--disable-dev-shm-usage',
    ],
  });

  try {
    // === Telas publicas (desktop) ===
    console.log('Capturando telas publicas (desktop 1440x900)...');
    const pub = await browser.newPage();
    await pub.setViewport({ width: 1440, height: 900, deviceScaleFactor: 1 });
    await shoot(pub, '/login', '01-login.png');
    await shoot(pub, '/register', '02-register.png');

    // === Telas autenticadas (desktop) ===
    console.log('Capturando telas autenticadas (desktop 1440x900)...');
    const auth = await browser.newPage();
    await auth.setViewport({ width: 1440, height: 900, deviceScaleFactor: 1 });
    await loginAndStoreToken(auth);
    await shoot(auth, '/jobs', '03-jobs-recommended.png');
    await shoot(auth, '/jobs/1', '04-job-detail.png', 1200);
    await shoot(auth, '/jobs?view=all&seniority=SENIOR', '05-jobs-filtered.png');
    await shoot(auth, '/profile', '06-profile.png');

    // === Mobile ===
    console.log('Capturando mobile (390x844)...');
    const mob = await browser.newPage();
    await mob.setViewport({ width: 390, height: 844, deviceScaleFactor: 2, isMobile: true });
    await loginAndStoreToken(mob);
    await shoot(mob, '/jobs', '07-jobs-mobile.png');
    await shoot(mob, '/jobs/1', '08-job-detail-mobile.png', 1200);

    // === Swagger UI ===
    console.log('Capturando Swagger UI...');
    const sw = await browser.newPage();
    await sw.setViewport({ width: 1440, height: 900, deviceScaleFactor: 1 });
    await sw.goto(`${API}/swagger-ui/index.html`, { waitUntil: 'networkidle0' });
    await new Promise((r) => setTimeout(r, 2000));
    await sw.screenshot({ path: `${OUT}/09-swagger.png` });
    console.log('  + 09-swagger.png');

    console.log('Pronto.');
  } finally {
    await browser.close();
  }
})().catch((err) => {
  console.error('ERRO:', err);
  process.exit(1);
});
