import { useState, useEffect } from 'react';
import { AuthContext } from './AuthContext';
import { api } from '../api';

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api('GET', '/api/auth/me')
            .then(data => setUser(data))
            .catch(() => setUser(null))
            .finally(() => setLoading(false));
    }, []);

    async function login(username, password) {
        const data = await api('POST', '/api/auth/login', { username, password });
        setUser(data);
        return data;
    }

    async function logout() {
        await fetch('/api/auth/logout', {
            method: 'POST',
            credentials: 'include',
        });
        setUser(null);
    }

    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}