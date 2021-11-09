package com.carvajal.adn.model;

import com.carvajal.adn.model.GraphString;
import com.carvajal.adn.model.ResultDescription;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Table(name ="result")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = GraphString.class)
public class Result {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private ResultDescription resultDescription;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private GraphString GraphString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResultDescription getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(ResultDescription resultDescription) {
        this.resultDescription = resultDescription;
    }

    public GraphString getGraphString() {
        return GraphString;
    }

    public void setGraphString(GraphString graphString) {
        GraphString = graphString;
    }
}
