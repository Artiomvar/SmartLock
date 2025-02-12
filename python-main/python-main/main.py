import pandas as pd
import joblib
import os
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from flask import Flask, request, jsonify

# Flask app setup
app = Flask(__name__)

# Paths
MODEL_PATH = "random_forest_model.pkl"
DATA_PATH = "realistic_dummy_data.csv"

def generate_usual_time(df):
    """
    Generate the 'Usual_Time' column based on predefined rules.
    """
    def is_usual_time(row):
        time_in_minutes = row["Hour"] * 60 + row["Minute"]
        day = row["Day_of_Week"]
        user = row["User_ID"]

        if user in [1, 2, 3, 4]:
            if day < 5:  # Weekday
                return 1 if 960 <= time_in_minutes <= 1200 else 0  # 16:00 to 20:00
            else:  # Weekend
                return 1 if time_in_minutes <= 60 else 0  # 00:00 to 01:00
        elif user == 5:  # User 5
            return 1 if 1320 <= time_in_minutes <= 1439 else 0  # 22:00 to 23:59
        return 0

    df["Usual_Time"] = df.apply(is_usual_time, axis=1)
    return df

def train_and_save_model():
    """
    Train a Random Forest model and save it to disk.
    """
    if not os.path.exists(DATA_PATH):
        raise FileNotFoundError(f"Training data file not found at {DATA_PATH}.")

    # Load the dataset
    df = pd.read_csv(DATA_PATH)

    # Generate 'Usual_Time' if not already present
    if "Usual_Time" not in df.columns:
        df = generate_usual_time(df)

    # Feature Engineering
    df["time_in_minutes"] = df["Hour"] * 60 + df["Minute"]

    # Define features and target
    X = df[["time_in_minutes", "Day_of_Week", "User_ID"]]
    y = df["Usual_Time"]

    # Split into training and test sets
    X_train, _, y_train, _ = train_test_split(X, y, test_size=0.2, random_state=42)

    # Train the model
    model = RandomForestClassifier(random_state=42)
    model.fit(X_train, y_train)

    # Save the model to disk
    joblib.dump(model, MODEL_PATH)
    print("Model trained and saved successfully.")
    return model

# Load or create the pre-trained model
def load_or_train_model():
    """
    Load an existing model or train a new one if not found.
    """
    if os.path.exists(MODEL_PATH):
        print("Model file found. Loading the model...")
        return joblib.load(MODEL_PATH)
    else:
        print("Model file not found. Training a new model...")
        return train_and_save_model()

# Load the model
model = load_or_train_model()

# Define preprocessing function
def preprocess_data(data):
    """
    Preprocess input data for prediction.
    :param data: List of dictionaries with keys 'Hour', 'Minute', 'Day_of_Week', and 'User_ID'
    :return: Preprocessed Pandas DataFrame
    """
    df = pd.DataFrame(data)
    df["time_in_minutes"] = df["Hour"] * 60 + df["Minute"]
    return df

@app.route('/predict', methods=['POST'])
def predict():
    """
    Flask endpoint to receive data and return predictions.
    :return: JSON response with prediction results
    """
    try:
        # Get JSON data from request
        input_data = request.get_json()

        # Validate input data
        if not input_data or not isinstance(input_data, list):
            return jsonify({"error": "Invalid input format. Expecting a list of data points."}), 400

        # Preprocess the data
        preprocessed_data = preprocess_data(input_data)

        # Ensure required columns are present
        required_columns = ["time_in_minutes", "Day_of_Week", "User_ID"]
        if not all(col in preprocessed_data.columns for col in required_columns):
            return jsonify({"error": "Missing required fields in input data."}), 400

        # Perform prediction
        predictions = model.predict(preprocessed_data[required_columns])

        # Determine result as "usual" or "unusual"
        result = "usual" if all(predictions) else "unusual"

        return jsonify({"result": result}), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    # Run the Flask app
    app.run(host='0.0.0.0', port=5000)
