package com.example.littleProject.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "holiday")
public class Holiday {
    @Id
    @Column(name = "Holiday")
    private String holiday;
}
