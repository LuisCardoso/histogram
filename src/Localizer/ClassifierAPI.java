package Localizer;

import histogram.TrainingData;

import java.util.ArrayList;

public interface ClassifierAPI {
	
	
	/*Train classifier, to know what PMF Table to use */
	public void trainClassifier(ArrayList<TrainingData> trainingData);

}
