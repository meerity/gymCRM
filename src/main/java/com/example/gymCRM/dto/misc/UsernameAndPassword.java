package com.example.gymcrm.dto.misc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsernameAndPassword {
    private String username;
    private String password;
}
