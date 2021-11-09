package com.carvajal.adn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDTO {

    private Integer count_correct_dna;
    private Integer count_defect_dna;
    private float ratio;

    public Integer getCount_correct_dna() {
        return count_correct_dna;
    }

    public void setCount_correct_dna(Integer count_correct_dna) {

        this.count_correct_dna = count_correct_dna;
    }

    public Integer getCount_defect_dna() {
        return count_defect_dna;
    }

    public void setCount_defect_dna(Integer count_defect_dna) {

        this.count_defect_dna = count_defect_dna;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
