package com.example.fruitfall.introductions;

import com.example.fruitfall.spatialTransformation.SpatialTransformation;
import com.example.fruitfall.spatialTransformation.SpatialTransformationHorizMirror;
import com.example.fruitfall.spatialTransformation.SpatialTransformationMainDiagonalMirror;
import com.example.fruitfall.spatialTransformation.SpatialTransformationNone;
import com.example.fruitfall.spatialTransformation.SpatialTransformationRotationCCW;
import com.example.fruitfall.spatialTransformation.SpatialTransformationRotationCW;
import com.example.fruitfall.spatialTransformation.SpatialTransformationSecondDiagonalMirror;
import com.example.fruitfall.spatialTransformation.SpatialTransformationUTurn;
import com.example.fruitfall.spatialTransformation.SpatialTransformationVertMirror;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransitionManager {
    public static Transition getTransitionFromString(String stringTransitionData) {
        Transition answer;
        String combinatedTransformations;
        if (stringTransitionData == null || stringTransitionData.isEmpty()) {
            answer = new TransitionRandom();
        } else {
            List<Transition> listOfTransitions = new ArrayList<>();
            for (int i = 0 ; i < stringTransitionData.length() ; i++) {
                switch (stringTransitionData.charAt(i)) {
                    case 'a':
                        listOfTransitions.add(new TransitionUpward12121());
                        break;
                    case 'h':
                        listOfTransitions.add(new TransitionLineCrossing());
                        break;
                    case 't':
                        listOfTransitions.add(new TransitionRandomFromLeft());
                        break;
                    case 's':
                        listOfTransitions.add(new TransitionSpiral());
                        break;
                    case 'd':
                        listOfTransitions.add(new TransitionDiagonal());
                        break;
                    case 'f':
                        listOfTransitions.add(new TransitionFlowerBlooming());
                        break;
                    case 'g':
                        listOfTransitions.add(new TransitionGrid());
                        break;
                    case 'w':
                        listOfTransitions.add(new TransitionSwirl());
                        break;
                    case '-':
                    case '`':
                    case '/':
                    case '|':
                    case '>':
                    case '<':
                    case 'U': // Only letter in the translation part !
                    case '.':
                        listOfTransitions.get(listOfTransitions.size() - 1).addTransformationChar(stringTransitionData.charAt(i));
                        break;
                    case '@': // Must be followed by one of the letters below ! Otherwise, nasty behaviour !
                        if (stringTransitionData.charAt(i + 1) == 'F') {
                            combinatedTransformations = "|-<>"; // For flip
                        } else {
                            combinatedTransformations = ".";
                        }
                        for (i = 0 ; i < combinatedTransformations.length() ; i++) {
                            listOfTransitions.get(listOfTransitions.size()-1).addTransformationChar(combinatedTransformations.charAt(i));
                        }
                    break;
                    default :
                        break;
                }
            }
            answer = listOfTransitions.get(new Random().nextInt(listOfTransitions.size()));
        }
        answer.deploy();
        return answer;
    }

    public static SpatialTransformation getSpatialTransformationFromChar(char c, int x1, int y1, int x2, int y2) {
        System.out.println(c);
        switch (c) {
            case '|' : return (new SpatialTransformationHorizMirror(x1, y1, x2, y2));
            case '-' : return (new SpatialTransformationVertMirror(x1, y1, x2, y2));
            case '`' : return (new SpatialTransformationMainDiagonalMirror(x1, y1, x2, y2));
            case '/' : return (new SpatialTransformationSecondDiagonalMirror(x1, y1, x2, y2));
            case '>' : return (new SpatialTransformationRotationCW(x1, y1, x2, y2));
            case '<' : return (new SpatialTransformationRotationCCW(x1, y1, x2, y2));
            case 'U' : return (new SpatialTransformationUTurn(x1, y1, x2, y2));
            case '.' : return (new SpatialTransformationNone(x1, y1, x2, y2));
            default : return null;
        }
    }

    public static SpatialTransformation randomTransformationRotation(int x1, int y1, int x2, int y2) {
        Random rand = new Random();
        int chance = rand.nextInt(4);
        System.out.println(chance);
        switch (chance) {
            case 1 : return new SpatialTransformationUTurn(x1, y1, x2, y2);
            case 2 : return new SpatialTransformationVertMirror(x1, y1, x2, y2);
            case 3 : return new SpatialTransformationHorizMirror(x1, y1, x2, y2);
            default : return new SpatialTransformationNone(x1, y1, x2, y2);
        }
    }

    public static SpatialTransformation randomTransformation(int x1, int y1, int x2, int y2) {
        Random rand = new Random();
        int chance = rand.nextInt(8);
        switch (chance) {
            case 1 : return new SpatialTransformationHorizMirror(x1, y1, x2, y2);
            case 2 : return new SpatialTransformationVertMirror(x1, y1, x2, y2);
            case 3 : return new SpatialTransformationUTurn(x1, y1, x2, y2);
            case 4 : return new SpatialTransformationRotationCW(x1, y1, x2, y2);
            case 5 : return new SpatialTransformationRotationCCW(x1, y1, x2, y2);
            case 6 : return new SpatialTransformationMainDiagonalMirror(x1, y1, x2, y2);
            case 7 : return new SpatialTransformationSecondDiagonalMirror(x1, y1, x2, y2);
            default : return new SpatialTransformationNone(x1, y1, x2, y2);
        }
    }

}
