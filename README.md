# ☁️ CloudSim GUI Platform

A feature-rich **JavaFX-based GUI application** for cloud simulation using **CloudSim Plus**. This platform provides an intuitive graphical interface to configure, run, analyze, and compare cloud computing simulations — without writing a single line of simulation code manually.

---

## 🚀 Features

- 🖥️ **Interactive Simulation Dashboard** — Configure and launch cloud simulations visually
- ⚙️ **Simulation Configuration** — Set VMs, Cloudlets, Hosts, and scheduling policies via GUI
- 📊 **Results View** — Detailed tabular results for each simulation run
- 📈 **Visualization View** — Charts and graphs of simulation metrics
- 🧪 **Experiment Management** — Save, load, and manage multiple simulation experiments
- 🔁 **Experiment History** — Browse all past experiments with timestamps
- ⚖️ **Experiment Comparison** — Side-by-side comparison of multiple experiments
- 🔬 **Sensitivity Analysis** — Analyze how changing parameters affects simulation outcomes
- 📋 **Summary View** — High-level overview of simulation performance
- ℹ️ **About Section** — Project info and credits

---

## 🛠️ Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| JavaFX | 21.0.2 |
| CloudSim Plus | 8.0.0 |
| Maven | 3.x |
| Maven Shade Plugin | 3.5.3 |
| JavaFX Maven Plugin | 0.0.8 |

---

## 📁 Project Structure

```
cloudsim-gui-platform/
├── pom.xml                          # Maven build configuration
└── src/
    └── main/
        └── java/
            └── com/cloudsim/gui/
                ├── Main.java                        # Application entry point
                ├── SimulationApp.java               # JavaFX Application class
                ├── controller/
                │   └── MainController.java          # Main app controller
                ├── experiment/
                │   ├── ComparisonEngine.java        # Experiment comparison logic
                │   ├── Experiment.java              # Experiment data model
                │   └── ExperimentStore.java         # Experiment persistence
                ├── model/
                │   ├── CloudletResult.java          # Cloudlet result model
                │   └── SimulationConfig.java        # Simulation configuration model
                ├── sensitivity/
                │   ├── SensitivityAnalysis.java     # Core sensitivity analysis
                │   ├── SensitivityConfig.java       # Sensitivity configuration
                │   ├── SensitivityParameter.java    # Parameter definition
                │   ├── SensitivityRunner.java       # Sensitivity run executor
                │   ├── SensitivityRunResult.java    # Sensitivity result model
                │   └── SensitivityStore.java        # Sensitivity data store
                ├── simulation/
                │   └── BasicSimulation.java         # CloudSim Plus simulation core
                └── ui/
                    ├── AboutView.java               # About page UI
                    ├── AppLayout.java               # Main app layout
                    ├── ConfigView.java              # Configuration UI
                    ├── DashboardView.java           # Dashboard UI
                    ├── ExperimentCompareView.java   # Comparison UI
                    ├── ExperimentDetailView.java    # Experiment detail UI
                    ├── ExperimentHistoryView.java   # History UI
                    ├── ResultsView.java             # Results table UI
                    ├── SensitivityView.java         # Sensitivity analysis UI
                    ├── SimulationView.java          # Simulation run UI
                    ├── Styles.java                  # App-wide styles
                    ├── SummaryView.java             # Summary UI
                    └── VisualizationView.java       # Charts/graphs UI
```

---

## ⚡ Prerequisites

Before running this project, make sure you have installed:

- **Java 17+** → [Download JDK](https://adoptium.net/)
- **Maven 3.6+** → [Download Maven](https://maven.apache.org/download.cgi)

---

## 🔧 Setup & Run

### 1. Clone the Repository

```bash
git clone https://github.com/ayushmaan1010161/Cloudsim-GUI-PLatform.git
cd Cloudsim-GUI-PLatform
```

### 2. Install Dependencies

```bash
mvn clean install -DskipTests
```

### 3. Run the Application

```bash
mvn javafx:run
```

### 4. Build a Fat JAR (Optional)

```bash
mvn clean package
java -jar target/cloudsim-gui-platform-1.0-SNAPSHOT-shaded.jar
```

---

## 📸 Screenshots

> _Coming soon — add screenshots of the running application here!_

---

## 🧠 How It Works

1. **Configure** your simulation parameters (VMs, Hosts, Cloudlets) using the Config View
2. **Run** the simulation — CloudSim Plus handles the cloud environment simulation internally
3. **View Results** in tabular format with all Cloudlet execution details
4. **Visualize** performance metrics via charts
5. **Save** experiments for future reference
6. **Compare** multiple experiments side-by-side
7. **Analyze** sensitivity by tweaking individual parameters and observing impact

---

## 🔗 Dependencies

- [CloudSim Plus](https://cloudsimplus.org/) — Cloud simulation framework
- [JavaFX 21](https://openjfx.io/) — Modern Java GUI framework
- [Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/) — Fat JAR packaging

---

## 👨‍💻 Author

**Aayushmaan**  
GitHub: [@ayushmaan1010161](https://github.com/ayushmaan1010161)

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

> ⭐ If you found this project helpful, please give it a star on GitHub!
