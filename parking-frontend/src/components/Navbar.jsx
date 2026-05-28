import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/useAuth';

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    async function handleLogout() {
        await logout();
        navigate('/login');
    }

    return (
        <nav className="navbar">
            <div className="nav-container">
                <div className="nav-brand">
                    <span className="nav-p-icon">P</span>
                    <span className="nav-brand-text">PARKING<br />SYSTEM</span>
                </div>
                <ul className="nav-links">
                    <li><NavLink to="/dashboard">Dashboard</NavLink></li>
                    <li><NavLink to="/history">Historial</NavLink></li>
                    {user?.role === 'ROLE_ADMIN' && (
                        <li><NavLink to="/admin">Admin</NavLink></li>
                    )}
                </ul>
                <div className="nav-actions">
                    <button className="btn-logout" onClick={handleLogout}>
                        Cerrar sesión ({user?.username})
                    </button>
                </div>
            </div>
        </nav>
    );
}