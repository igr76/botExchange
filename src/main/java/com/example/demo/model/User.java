package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/** Сущность пользователя */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    /** Номер пользователя */
    @Id
    long chatId;
    /** Рублевый счёт */
    double balanceRub;
    /** Долларовый  счёт */
    double balanceUsd;
    /** Евро счёт */
    double balanceEur;
}
