// ============================================
// FASHIONSTORE — main.js
// ============================================

const BASE = 'http://localhost:8080/api';

// ---------- CART ----------
function getCart() {
  return JSON.parse(localStorage.getItem('fs_cart') || '[]');
}
function saveCart(c) {
  localStorage.setItem('fs_cart', JSON.stringify(c));
  updateCartBadge();
}
function updateCartBadge() {
  const total = getCart().reduce((s, i) => s + i.qty, 0);
  document.querySelectorAll('#cart-count').forEach(el => el.textContent = total);
}
function addToCart(id, name, price, img, cat) {
  const cart = getCart();
  const ex = cart.find(i => i.id === id);
  if (ex) ex.qty++;
  else cart.push({ id, name, price, img: img || '', cat: cat || '', qty: 1 });
  saveCart(cart);
  showToast('✅ ' + name + ' added to cart!');
}
function removeFromCart(id) {
  saveCart(getCart().filter(i => i.id !== id));
}
function changeQty(id, delta) {
  const cart = getCart();
  const item = cart.find(i => i.id === id);
  if (item) {
    item.qty += delta;
    if (item.qty <= 0) return removeFromCart(id);
    saveCart(cart);
  }
}

// ---------- USER ----------
function getUser() {
  return JSON.parse(localStorage.getItem('fs_user') || 'null');
}
function logout() {
  localStorage.removeItem('fs_user');
  window.location.href = 'login.html';
}

// ---------- TOAST ----------
function showToast(msg) {
  let wrap = document.getElementById('toast-wrap');
  if (!wrap) {
    wrap = document.createElement('div');
    wrap.id = 'toast-wrap';
    document.body.appendChild(wrap);
  }
  const t = document.createElement('div');
  t.className = 'toast';
  t.textContent = msg;
  wrap.appendChild(t);
  setTimeout(() => t.remove(), 3000);
}

// ---------- INIT ----------
document.addEventListener('DOMContentLoaded', updateCartBadge);