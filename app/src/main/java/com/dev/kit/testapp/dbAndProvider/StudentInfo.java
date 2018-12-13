package com.dev.kit.testapp.dbAndProvider;

import com.google.gson.Gson;

/**
 * Created by cuiyan on 2018/12/13.
 */

public class StudentInfo {
    private int studentNumber;
    private String name;
    private int gradeMathematics ;
    private int gradeChinese;
    private int gradePhysics;
    private int gradeChemistry ;

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGradeMathematics() {
        return gradeMathematics;
    }

    public void setGradeMathematics(int gradeMathematics) {
        this.gradeMathematics = gradeMathematics;
    }

    public int getGradeChinese() {
        return gradeChinese;
    }

    public void setGradeChinese(int gradeChinese) {
        this.gradeChinese = gradeChinese;
    }

    public int getGradePhysics() {
        return gradePhysics;
    }

    public void setGradePhysics(int gradePhysics) {
        this.gradePhysics = gradePhysics;
    }

    public int getGradeChemistry() {
        return gradeChemistry;
    }

    public void setGradeChemistry(int gradeChemistry) {
        this.gradeChemistry = gradeChemistry;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
