package com.javier.parking.service;

import com.javier.parking.model.User;
import com.javier.parking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese username");
        }
        return userRepository.save(user);
    }

    public void delete(Long id) {
        User existente = findById(id);
        userRepository.delete(existente);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public User update(Long id, User userActualizado){
        User existente = findById(id);
        existente.setRole(userActualizado.getRole());
        existente.setEnabled(userActualizado.isEnabled());
        return userRepository.save(existente);
    }
}
