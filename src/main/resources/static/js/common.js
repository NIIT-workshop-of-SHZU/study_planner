/**
 * 智能学习计划生成器 - 公共JS
 * 统一登录状态管理
 */

// API基础路径
const API_BASE = '/api';

// 全局用户状态
let currentUser = null;

/**
 * 检查登录状态（从服务器获取）
 * 这是唯一的登录状态来源
 */
async function checkLogin() {
    try {
        const response = await fetch(`${API_BASE}/user/info`, {
            credentials: 'include'
        });
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            currentUser = result.data;
            // 同步到 localStorage（仅用于显示，不作为登录判断依据）
            localStorage.setItem('user', JSON.stringify(result.data));
            return result.data;
        }
        
        currentUser = null;
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        return null;
    } catch (error) {
        console.error('检查登录状态失败:', error);
        currentUser = null;
        return null;
    }
}

/**
 * 获取当前用户（同步方法，返回缓存的用户信息）
 */
function getCurrentUser() {
    return currentUser;
}

/**
 * 判断是否已登录
 */
function isLoggedIn() {
    return currentUser !== null;
}

/**
 * 更新页面上所有登录相关的UI
 */
function updateLoginUI() {
    // 更新导航栏
    updateNavbarLoginStatus();
    
    // 触发自定义事件，让各页面可以响应
    window.dispatchEvent(new CustomEvent('loginStatusChanged', { 
        detail: { user: currentUser, isLoggedIn: isLoggedIn() }
    }));
}

/**
 * 更新导航栏的登录状态显示
 */
function updateNavbarLoginStatus() {
    const userMenu = document.getElementById('userMenu');
    const loginBtn = document.getElementById('loginBtn');
    const usernameSpan = document.getElementById('username');
    const userNav = document.getElementById('userNav');
    
    if (isLoggedIn()) {
        // 已登录状态
        if (userMenu) userMenu.style.display = 'block';
        if (loginBtn) loginBtn.style.display = 'none';
        if (usernameSpan) usernameSpan.textContent = currentUser.username;
        
        // 更新 userNav（用于主页）
        if (userNav) {
            userNav.innerHTML = `
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i> ${currentUser.username}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="pages/dashboard.html"><i class="bi bi-speedometer2"></i> 仪表盘</a></li>
                        <li><a class="dropdown-item" href="pages/my-plans.html"><i class="bi bi-journal-text"></i> 我的计划</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" onclick="logout()"><i class="bi bi-box-arrow-right"></i> 退出登录</a></li>
                    </ul>
                </li>
            `;
        }
    } else {
        // 未登录状态
        if (userMenu) userMenu.style.display = 'none';
        if (loginBtn) loginBtn.style.display = 'block';
        
        // 更新 userNav（用于主页）
        if (userNav) {
            userNav.innerHTML = `
                <li class="nav-item">
                    <a class="nav-link" href="pages/login.html">登录</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="pages/register.html">注册</a>
                </li>
            `;
        }
    }
}

/**
 * 初始化登录状态
 * 所有页面加载时都应该调用这个函数
 */
async function initLoginStatus() {
    await checkLogin();
    updateLoginUI();
    return currentUser;
}

/**
 * 登录
 */
async function doLogin(username, password) {
    try {
        const response = await fetch(`${API_BASE}/user/login`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            currentUser = result.data;
            localStorage.setItem('user', JSON.stringify(result.data));
            updateLoginUI();
            return { success: true, user: result.data };
        } else {
            return { success: false, message: result.message || '登录失败' };
        }
    } catch (error) {
        console.error('登录失败:', error);
        return { success: false, message: '网络错误，请稍后重试' };
    }
}

/**
 * 登出
 */
async function logout() {
    try {
        await fetch(`${API_BASE}/user/logout`, {
            method: 'POST',
            credentials: 'include'
        });
    } catch (error) {
        console.error('登出请求失败:', error);
    }
    
    // 清除本地状态
    currentUser = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('aiChatHistory');
    
    // 跳转到首页
    window.location.href = '/index.html';
}

/**
 * 注册
 */
async function doRegister(username, password, email) {
    try {
        const response = await fetch(`${API_BASE}/user/register`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, email })
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            return { success: true, message: '注册成功' };
        } else {
            return { success: false, message: result.message || '注册失败' };
        }
    } catch (error) {
        console.error('注册失败:', error);
        return { success: false, message: '网络错误，请稍后重试' };
    }
}

/**
 * API请求封装（自动处理登录状态）
 */
async function apiRequest(url, options = {}) {
    const defaultOptions = {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        }
    };
    
    const mergedOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };
    
    try {
        const response = await fetch(`${API_BASE}${url}`, mergedOptions);
        const result = await response.json();
        
        if (result.code === 401) {
            // 登录失效，更新状态
            currentUser = null;
            localStorage.removeItem('user');
            localStorage.removeItem('token');
            updateLoginUI();
            
            // 显示提示
            showToast('登录已过期，请重新登录', 'warning');
            return null;
        }
        
        return result;
    } catch (error) {
        console.error('API请求失败:', error);
        showToast('网络请求失败，请稍后重试', 'error');
        return null;
    }
}

/**
 * 需要登录的页面检查
 * 如果未登录，跳转到登录页
 */
async function requireLogin(redirectUrl) {
    const user = await checkLogin();
    if (!user) {
        const returnUrl = encodeURIComponent(window.location.href);
        window.location.href = `/pages/login.html?redirect=${returnUrl}`;
        return false;
    }
    updateLoginUI();
    return true;
}

// ==================== 工具函数 ====================

/**
 * 显示提示消息
 */
function showToast(message, type = 'info') {
    let toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '9999';
        document.body.appendChild(toastContainer);
    }
    
    const toastId = 'toast-' + Date.now();
    const bgClass = {
        'success': 'bg-success',
        'error': 'bg-danger',
        'warning': 'bg-warning',
        'info': 'bg-info'
    }[type] || 'bg-info';
    
    const toastHtml = `
        <div id="${toastId}" class="toast ${bgClass} text-white" role="alert">
            <div class="toast-body d-flex justify-content-between align-items-center">
                ${message}
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();
    
    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

/**
 * 显示加载中
 */
function showLoading(element, text = '加载中...') {
    if (element) {
        element.innerHTML = `
            <div class="text-center py-4">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="mt-2 text-muted">${text}</p>
            </div>
        `;
    }
}

/**
 * 格式化日期
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

/**
 * 格式化日期时间
 */
function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * HTML转义
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * 截断文本
 */
function truncateText(text, maxLength) {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// ==================== 页面初始化 ====================

// 页面加载完成后自动初始化登录状态
document.addEventListener('DOMContentLoaded', () => {
    initLoginStatus();
});
