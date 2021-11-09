package com.carvajal.adn.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@SequenceGenerator(name = "result_description_gen", sequenceName = "result_description_gen",  initialValue = 0)
@Table(name ="result_description")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = ResultDescription.class)
public class ResultDescription {

    public static final Integer CORRECT_DNA = 1;

    public static final Integer DEFECT_DNA = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "result_description_gen")
    private Integer id;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
