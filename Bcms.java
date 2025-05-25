package bcms;

//import bcms.model.User;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;
import java.util.stream.Collectors;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.*;





public class Bcms extends Application {
   // ক্লাস ফিল্ডে যোগ করো (userJoinedClubs map)
    private final Map<String, List<String>> userJoinedClubs = new HashMap<>();

    private final String EVENTS_FILE = "events.json";
    private final String CLUBS_FILE = "clubs.json";
    private final String USERS_FILE = "users.json";

    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    //private final ObservableList<Club> allClubs = FXCollections.observableArrayList();


    private String username;
    private String password;
    private String role;
    private List<String> activities = new ArrayList<>();
// --- Events ---
    private void saveEventsToJson() {
        try (Writer writer = new FileWriter(EVENTS_FILE)) {
            new Gson().toJson(new ArrayList<>(allEvents), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadEventsFromJson() {
        File file = new File(EVENTS_FILE);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(EVENTS_FILE)) {
            Type listType = new TypeToken<List<Event>>(){}.getType();
            List<Event> loaded = new Gson().fromJson(reader, listType);
            allEvents.setAll(loaded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Clubs ---
    private void saveClubsToJson() {
        try (Writer writer = new FileWriter(CLUBS_FILE)) {
            new Gson().toJson(new ArrayList<>(allClubs), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadClubsFromJson() {
        File file = new File(CLUBS_FILE);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(CLUBS_FILE)) {
            Type listType = new TypeToken<List<Club>>(){}.getType();
            List<Club> loaded = new Gson().fromJson(reader, listType);
            allClubs.setAll(loaded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Users ---
    private void saveUsersToJson() {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            new Gson().toJson(new ArrayList<>(allUsers), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadUsersFromJson() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(USERS_FILE)) {
            Type listType = new TypeToken<List<User>>(){}.getType();
            List<User> loaded = new Gson().fromJson(reader, listType);
            allUsers.setAll(loaded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

public static class User {
        private String username;
        private String password;
        private String role;
        private List<String> activities = new ArrayList<>();

        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
        public List<String> getActivities() { return activities; }
        public void addActivity(String activity) { activities.add(activity); }
    }


    private final ObservableList<Club> allClubs = FXCollections.observableArrayList(
    new Club("Environment and Sustainability Club ", "Technical", "CIVIL", "Work hard you get more", "Mehedi", "https://www.facebook.com/profile.php?id=61565232606575"),
    new Club("Baust IPE Club ", "Technical", "IPE", "Work hard you get more", "Sadik", "https://www.facebook.com/profile.php?id=61567779073166"),
    new Club("BAUST Cultural Society ", "Cultural", "BBA", "Work hard you get more", "Ridoy", "https://www.facebook.com/share/1Drhckzmyx/"),
    new Club("Baust English language club", "Cultural", "English", "Core Language is the English", "Mehedi", "https://www.facebook.com/groups/2926733157341939/permalink/9363772723637918/"),
    new Club("Career society Club ", "Technical", "BBA", "Work hard you get more", "Shaon", "https://www.facebook.com/share/1BbZ7TpP3J/"),
    new Club("BAUST COMPUTER CLUB", "Technical", "CSE", "Work hard you get more", "Hasib", "https://www.facebook.com/BaustComputerClub"),
    new Club("BAUST Mechatronics and Robotics Club ", "Technical", "EEE", "Work hard you get more", "Niloy", "https://www.facebook.com/share/1682Ke4AgT/")
    


);

   private final ObservableList<Event> allEvents = FXCollections.observableArrayList(
    new Event("CSE Fest 2025", "Tech Club", "25th August", "CSE Dept", "A mega tech event", "https://www.example.com/csefest2025"),
    new Event("Cultural Night", "Cultural Club", "September", "English Dept", "Annual cultural festival", "https://www.example.com/culturalnight")
);

    private final List<String> departments = Arrays.asList("CSE", "EEE", "ME", "IPE", "CE", "ICT", "BBA", "BBA in AIS", "English");
    private final Map<String, String> userPass = new HashMap<String, String>() {{
        put("superadmin", "superadmin123");
        put("deptadmin", "deptadmin123");
        put("moderator", "moderator123");
    }};
    private final Map<String, String> userRole = new HashMap<String, String>() {{
        put("superadmin", "SUPER_ADMIN");
        put("deptadmin", "DEPT_ADMIN");
        put("moderator", "MODERATOR");
    }};
    private String loggedInUser = null;
    private String loggedInRole = null;

    private BorderPane root;
    private Scene scene;
    private VBox mainUI, loginUI, regUI, adminUI;
    private MenuBar menuBar;
    private HBox footer;
    private VBox adminSidebar;
    private VBox adminMainContent;
    private ObservableList<Club> filteredClubs = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
//        loadEventsFromJson();
//        loadClubsFromJson();
//        loadUsersFromJson();
       root = new BorderPane(); // <-- FIRST: initialize root

        setupMenuBar();
        setupFooter();
        setupMainUI();
        setupLoginUI();
        setupRegistrationUI();
        setupAdminUI();

//        root = new BorderPane();
        showLoginUI();

        scene = new Scene(root, 1100, 900);

        String menuBarCss =
            ".menu-bar {\n" +
            "    -fx-background-color: #232c2f;\n" +
            "    -fx-border-radius: 14;\n" +
            "    -fx-background-radius: 14;\n" +
            "    -fx-padding: 0 18 0 18;\n" +
            "    -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.10),8,0,0,2);\n" +
            "    -fx-min-height: 64px;\n" +
            "}\n" +
            ".menu-bar .label {\n" +
            "    -fx-text-fill: white;\n" +
            "    -fx-font-size: 22px;\n" +
            "    -fx-font-family: 'Arial';\n" +
            "    -fx-font-weight: 400;\n" +
            "}\n" +
            ".menu .context-menu {\n" +
            "    -fx-background-color: #fff;\n" +
            "    -fx-background-radius: 10;\n" +
            "    -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.12),8,0,0,2);\n" +
            "}\n" +
            ".menu-item .label {\n" +
            "    -fx-text-fill: #222;\n" +
            "    -fx-font-size: 18px;\n" +
            "    -fx-font-family: 'Arial';\n" +
            "}\n" +
            ".menu-item:focused .label {\n" +
            "    -fx-text-fill: #19706a;\n" +
            "}\n" +
            ".menu-bar .menu {\n" +
            "    -fx-background-radius: 10;\n" +
            "    -fx-padding: 0 18 0 18;\n" +
            "}\n";
        scene.getStylesheets().add("data:text/css," + menuBarCss.replace("\n", "%0A").replace(" ", "%20"));

        primaryStage.setTitle("BAUST Club Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

  // This is the card-based clubs page for Home/MenuBar (for all users and admin)
    
    private void setupMenuBar() {
    menuBar = new MenuBar();

    // Home menu with clickable item
    Menu home = new Menu("Home");
    MenuItem homeItem = new MenuItem("Home");
    homeItem.setOnAction(e -> showMainUI());
    home.getItems().add(homeItem);
    

    Menu clubsMenu = new Menu("Clubs");
    MenuItem allClubs = new MenuItem("All Clubs");
    MenuItem techClubs = new MenuItem("Technical Clubs");
    MenuItem cultClubs = new MenuItem("Cultural Clubs");
    MenuItem sportsClubs = new MenuItem("Sports Clubs");
    clubsMenu.getItems().addAll(allClubs, techClubs, cultClubs, sportsClubs);

    Menu events = new Menu("Events");
    MenuItem allEventsItem = new MenuItem("All Events");
    allEventsItem.setOnAction(e -> showHomeEventsPage());
    events.getItems().add(allEventsItem);
    
    Menu profile = new Menu("My Profile");
MenuItem profileItem = new MenuItem("View Profile");
profileItem.setOnAction(e -> showProfilePage());
profile.getItems().add(profileItem);



    // Logout menu
    Menu logoutMenu = new Menu("Logout");
    MenuItem logoutItem = new MenuItem("Logout");
    logoutMenu.getItems().add(logoutItem);

    // Department-wise Clubs submenu
    Menu departmentWiseMenu = new Menu("Department-wise Clubs");
    for (String dept : departments) {
        MenuItem deptItem = new MenuItem(dept + " Department");
        deptItem.setOnAction(e -> showHomeClubsPage("department", dept));
        departmentWiseMenu.getItems().add(deptItem);
    }
    clubsMenu.getItems().add(departmentWiseMenu);

    menuBar.getMenus().addAll(home, clubsMenu, events, profile, logoutMenu);

    // Event Handlers for clubs and others
    allClubs.setOnAction(e -> showHomeClubsPage("all", null));
    techClubs.setOnAction(e -> showHomeClubsPage("type", "Technical"));
    cultClubs.setOnAction(e -> showHomeClubsPage("type", "Cultural"));
    sportsClubs.setOnAction(e -> showHomeClubsPage("type", "Sports"));
//    events.setOnMenuValidation(e -> showHomeEventsPage());
//    profile.setOnMenuValidation(e -> showProfilePage());

    // Logout Action
    logoutItem.setOnAction(e -> {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            loggedInUser = null;
            loggedInRole = null;
            root.setTop(null);
            root.setLeft(null);
            root.setCenter(null);
            root.setBottom(null);
            showLoginUI();
        }
    });
}
   

    private void showHomeEventsPage() {
    // Title
    Text title = new Text("All Events");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
    title.setStyle("-fx-fill: #212121;");

    // Get events sorted by date (upcoming first)
    List<Event> sortedEvents = getSorted1Events(); // Make sure this is implemented

    // Event cards container
    FlowPane eventsFlow = new FlowPane();
    eventsFlow.setHgap(28);
    eventsFlow.setVgap(22);
    eventsFlow.setPadding(new Insets(18, 36, 18, 36));
    eventsFlow.setAlignment(Pos.TOP_LEFT);

    if (sortedEvents.isEmpty()) {
        Label noEvents = new Label("No events found.");
        noEvents.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        eventsFlow.getChildren().add(noEvents);
    } else {
        for (Event ev : sortedEvents) {
            VBox card = new VBox(10);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(14, 22, 14, 22));
            card.setSpacing(6);
            card.setMinWidth(230);
            card.setStyle(
                "-fx-background-color: #fff; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #19706a; " +
                "-fx-border-radius: 12;"
            );

            // Make event name clickable
            
            
            Hyperlink evTitle = new Hyperlink(ev.getTitle());
            evTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            evTitle.setBorder(Border.EMPTY);
            evTitle.setStyle("-fx-text-fill: #212121;");
            evTitle.setOnAction(ae -> {
    String link = ev.getLink();
    if (link != null && !link.trim().isEmpty()) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(link));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } else {
        // Optionally, show an alert if no link is available
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Link");
        alert.setHeaderText(null);
        alert.setContentText("No link available for this event.");
        alert.showAndWait();
    }
});

            Text evClub = new Text("Club: " + ev.getClubName());
            evClub.setFont(Font.font("Arial", 15));
            evClub.setStyle("-fx-fill: #374151;");
            Text evDate = new Text("Date: " + ev.getDateString());
            evDate.setFont(Font.font("Arial", 15));
            evDate.setStyle("-fx-fill: #374151;");

            Button detailsBtn = new Button(ev.isUpcoming() ? "Register" : "Details");
            detailsBtn.setStyle(
                "-fx-background-color: #19706a; " +
                "-fx-text-fill: #fff; " +
                "-fx-font-size: 15px; " +
                "-fx-background-radius: 8;"
            );
            detailsBtn.setOnAction(e -> showEventDetails(ev));
            card.getChildren().addAll(evTitle, evClub, evDate, detailsBtn);
            eventsFlow.getChildren().add(card);
        }
    }

    VBox content = new VBox(18, title, eventsFlow);
    content.setAlignment(Pos.TOP_CENTER);
    content.setPadding(new Insets(24, 0, 24, 0));
    content.setStyle("-fx-background-color: #f7faf7;");

    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background: transparent;");

    root.setTop(menuBar); // or your topBar if you use a custom one
    root.setLeft(null);
    root.setCenter(scroll);
    root.setBottom(footer);
}


// Returns a list of events sorted by date (upcoming first)
private List<Event> getSorted1Events() {
    List<Event> sorted = new ArrayList<>(allEvents); // allEvents is your event list
    sorted.sort(Comparator.comparing(Event::getDate)); // Assumes Event has getDate() returning LocalDate
    return sorted;
}


//private void showHomeClubsPage(String filterType, String filterValue) {
//    VBox headerBox = new VBox(3);
//    Text title = new Text("All Clubs");
//    title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
//    title.setStyle("-fx-fill: #212121;");
//    headerBox.getChildren().add(title);
//    headerBox.setAlignment(Pos.CENTER_LEFT);
//
//    // Filter clubs
//    ObservableList<Club> clubsToShow = allClubs;
//    if (filterType != null && filterValue != null) {
//        if (filterType.equals("type")) {
//            clubsToShow = FXCollections.observableArrayList(
//                allClubs.stream()
//                    .filter(club -> club.getType().equalsIgnoreCase(filterValue))
//                    .collect(Collectors.toList())
//            );
//        } else if (filterType.equals("department")) {
//            clubsToShow = FXCollections.observableArrayList(
//                allClubs.stream()
//                    .filter(club -> club.getDepartment().equalsIgnoreCase(filterValue))
//                    .collect(Collectors.toList())
//            );
//        }
//    }
//
//    HBox clubsBox = new HBox(24);
//    clubsBox.setAlignment(Pos.CENTER_LEFT);
//    updateClubCards(clubsBox, clubsToShow);
//
//    VBox clubsSection = new VBox(12, headerBox, clubsBox);
//    clubsSection.setPadding(new Insets(36, 32, 36, 32));
//    clubsSection.setStyle("-fx-background-color: #fff; -fx-background-radius: 16;");
//
//    ScrollPane scroll = new ScrollPane(clubsSection);
//    scroll.setFitToWidth(true);
//    scroll.setStyle("-fx-background: transparent;");
//
//    VBox clubsPage = new VBox(scroll);
//    root.setTop(menuBar);
//    root.setLeft(null);
//    root.setCenter(clubsPage);
//    root.setBottom(footer);
//}

private void showHomeClubsPage(String filterType, String filterValue) {
    VBox headerBox = new VBox(3);
    Text title = new Text("All Clubs");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
    title.setStyle("-fx-fill: #212121;");
    headerBox.getChildren().add(title);
    headerBox.setAlignment(Pos.CENTER_LEFT);

    // Filter clubs
    ObservableList<Club> clubsToShow = allClubs;
    if (filterType != null && filterValue != null) {
        if (filterType.equals("type")) {
            clubsToShow = FXCollections.observableArrayList(
                allClubs.stream()
                    .filter(club -> club.getType().equalsIgnoreCase(filterValue))
                    .collect(Collectors.toList())
            );
        } else if (filterType.equals("department")) {
            clubsToShow = FXCollections.observableArrayList(
                allClubs.stream()
                    .filter(club -> club.getDepartment().equalsIgnoreCase(filterValue))
                    .collect(Collectors.toList())
            );
        }
    }

    // Use FlowPane instead of HBox for 3 cards per row
    FlowPane clubsBox = new FlowPane();
    clubsBox.setHgap(24); // horizontal gap between cards
    clubsBox.setVgap(24); // vertical gap between rows
    clubsBox.setAlignment(Pos.TOP_LEFT);
    clubsBox.setPrefWrapLength(3 * 340); // 3 cards per row (adjust 340 as your card width + gap)

    updateClubCards(clubsBox, clubsToShow);

    VBox clubsSection = new VBox(12, headerBox, clubsBox);
    clubsSection.setPadding(new Insets(36, 32, 36, 32));
    clubsSection.setStyle("-fx-background-color: #fff; -fx-background-radius: 16;");

    ScrollPane scroll = new ScrollPane(clubsSection);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background: transparent;");

    VBox clubsPage = new VBox(scroll);
    root.setTop(menuBar);
    root.setLeft(null);
    root.setCenter(clubsPage);
    root.setBottom(footer);
}





    private void setupFooter() {
        footer = new HBox(30);
        Button contact = new Button("Contact Us");
        Button help = new Button("Help");
        Button about = new Button("About the Developers");
        styleFooterButton(contact);
        styleFooterButton(help);
        styleFooterButton(about);
        footer.getChildren().addAll(contact, help, about);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(12, 0, 12, 0));
        footer.setStyle("-fx-background-color: #fff; -fx-border-radius: 0 0 18 18;");
        contact.setOnAction(e -> showContactDialog());
        help.setOnAction(e -> showHelpPage());
        about.setOnAction(e -> showAboutDevelopers());
    }
    private void styleFooterButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #19706a; -fx-font-size: 16px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #19706a; -fx-text-fill: #fff; -fx-font-size: 16px; -fx-background-radius: 7;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #19706a; -fx-font-size: 16px;"));
    }

//    private void setupMainUI() {
//        VBox headerBox = new VBox(3);
//        Text title = new Text("BAUST Club Management System");
//        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
//        title.setStyle("-fx-fill: #212121;");
//        Text subtitle = new Text("One Platform for All BAUST Clubs");
//        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
//        subtitle.setStyle("-fx-fill: #444;");
//        headerBox.getChildren().addAll(title, subtitle);
//        headerBox.setAlignment(Pos.CENTER);
//
//        HBox searchBox = new HBox(12);
//        TextField searchField = new TextField();
//        searchField.setPromptText("Search clubs, department or type...");
//        searchField.setPrefWidth(220);
//        searchField.setFont(Font.font("Arial", 16));
//        Button exploreBtn = new Button("Explore Clubs");
//        Button joinBtn = new Button("Join Now");
//        styleMainButton(exploreBtn);
//        styleMainButton(joinBtn);
//        searchBox.getChildren().addAll(searchField, exploreBtn, joinBtn);
//        searchBox.setAlignment(Pos.CENTER);
//
//        exploreBtn.setOnAction(e -> showHomeClubsPage("all", null));
//        joinBtn.setOnAction(e -> showLoginUI());
//
//        VBox clubsSection = new VBox(7);
//        Text clubsTitle = new Text("Featured Clubs");
//        clubsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
//        clubsTitle.setStyle("-fx-fill: #212121;");
//        HBox clubsBox = new HBox(24);
//        clubsBox.setAlignment(Pos.CENTER);
//
//        filteredClubs.setAll(allClubs);
//        updateClubCards(clubsBox, filteredClubs);
//
//        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
//            filteredClubs.setAll(allClubs.stream()
//                .filter(club -> club.getName().toLowerCase().contains(newVal.toLowerCase()) ||
//                                club.getType().toLowerCase().contains(newVal.toLowerCase()) ||
//                                club.getDepartment().toLowerCase().contains(newVal.toLowerCase()))
//                .collect(Collectors.toList()));
//            updateClubCards(clubsBox, filteredClubs);
//        });
//
//        clubsSection.getChildren().addAll(clubsTitle, clubsBox);
//
//        VBox eventsSection = new VBox(7);
//        Text eventsTitle = new Text("Upcoming Events");
//        eventsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
//        eventsTitle.setStyle("-fx-fill: #212121;");
//        HBox eventsBox = new HBox(24);
//        eventsBox.setAlignment(Pos.CENTER);
//
//        for (Event ev : allEvents) {
//            VBox eventCard = new VBox(8);
//            eventCard.setPadding(new Insets(16));
//            eventCard.setStyle("-fx-background-color: #fff; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-background-radius: 12;");
//            Text eventTitle = new Text("Event: " + ev.title);
//            eventTitle.setFont(Font.font("Arial", 16));
//            Text eventDate = new Text("Date: " + ev.date);
//            Button detailsBtn = new Button(ev.title.equals("Cultural Night") ? "Register" : "Details");
//            styleMainButton(detailsBtn);
//            eventCard.getChildren().addAll(eventTitle, eventDate, detailsBtn);
//            eventsBox.getChildren().add(eventCard);
//
//            detailsBtn.setOnAction(e -> showEventDetails(ev));
//        }
//        eventsSection.getChildren().addAll(eventsTitle, eventsBox);
//
//        VBox whyBox = new VBox(8);
//        whyBox.setPadding(new Insets(16));
//        Text whyTitle = new Text("Why Use This System?");
//        whyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//        whyTitle.setStyle("-fx-fill: #212121;");
//        ListView<String> whyList = new ListView<>();
//        whyList.getItems().addAll(
//                "One-click Club Join Request",
//                "Event Registration System",
//                "All Club Info in One Place",
//                "Direct Social Links to Clubs");
//        whyList.setMaxHeight(90);
//        whyList.setStyle("-fx-background-color: #f8f9f8; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-background-radius: 10;");
//
//        Button loginBtn = new Button("Login");
//        Button signupBtn = new Button("Sign Up");
//        styleMainButton(loginBtn);
//        styleMainButton(signupBtn);
//        HBox loginSignupBox = new HBox(18, loginBtn, signupBtn);
//        loginSignupBox.setAlignment(Pos.CENTER);
//
//        whyBox.getChildren().addAll(whyTitle, whyList, loginSignupBox);
//
//        loginBtn.setOnAction(e -> showLoginUI());
//        signupBtn.setOnAction(e -> showRegistrationUI());
//
//        VBox mainContent = new VBox(18, headerBox, searchBox, clubsSection, eventsSection, whyBox);
//        mainContent.setPadding(new Insets(28));
//        mainContent.setAlignment(Pos.TOP_CENTER);
//        mainContent.setStyle("-fx-background-color: #f6f6f3; -fx-border-radius: 18; -fx-background-radius: 18;");
//
//        ScrollPane scroll = new ScrollPane(mainContent);
//        scroll.setFitToWidth(true);
//        scroll.setStyle("-fx-background:transparent;");
//
//        mainUI = new VBox(scroll);
//// --- Admin Back Button in MenuBar ---
//if (loggedInUser != null && (loggedInRole.equals("SUPER_ADMIN") || loggedInRole.equals("DEPT_ADMIN"))) {
//    // Remove if already present (avoid duplicate)
//    menuBar.getMenus().removeIf(m -> m.getText().equals("Back to Admin Panel"));
//
//    Menu backAdminMenu = new Menu("Back to Admin Panel");
//    backAdminMenu.setStyle("-fx-text-fill: #fff; -fx-font-size: 18px;");
//
//    // Use a MenuItem for click action
//    MenuItem backItem = new MenuItem("Return");
//    backItem.setOnAction(e -> {
//        setupAdminUI();
//        root.setTop(null);
//        root.setLeft(adminSidebar);
//        root.setCenter(adminMainContent);
//    });
//    backAdminMenu.getItems().add(backItem);
//
//    // Insert before Logout menu if possible
//    int logoutIdx = menuBar.getMenus().size();
//    for (int i = 0; i < menuBar.getMenus().size(); i++) {
//        if (menuBar.getMenus().get(i).getText().equals("Logout")) {
//            logoutIdx = i;
//            break;
//        }
//    }
//    menuBar.getMenus().add(logoutIdx, backAdminMenu);
//} else {
//    // Remove if not admin
//    menuBar.getMenus().removeIf(m -> m.getText().equals("Back to Admin Panel"));
//}
//    }
//

    private void setupMainUI() {
    VBox headerBox = new VBox(3);
    Text title = new Text("BAUST Club Management System");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
    title.setStyle("-fx-fill: #212121;");
    Text subtitle = new Text("One Platform for All BAUST Clubs");
    subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
    subtitle.setStyle("-fx-fill: #444;");
    headerBox.getChildren().addAll(title, subtitle);
    headerBox.setAlignment(Pos.CENTER);

    HBox searchBox = new HBox(12);
    TextField searchField = new TextField();
    searchField.setPromptText("Search clubs, department or type...");
    searchField.setPrefWidth(220);
    searchField.setFont(Font.font("Arial", 16));
    Button exploreBtn = new Button("Explore Clubs");
    Button joinBtn = new Button("Join Now");
    styleMainButton(exploreBtn);
    styleMainButton(joinBtn);
    searchBox.getChildren().addAll(searchField, exploreBtn, joinBtn);
    searchBox.setAlignment(Pos.CENTER);

    exploreBtn.setOnAction(e -> showHomeClubsPage("all", null));
    joinBtn.setOnAction(e -> showLoginUI());

    // --- Featured Clubs Section ---
    VBox clubsSection = new VBox(7);
    Text clubsTitle = new Text("Featured Clubs");
    clubsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
    clubsTitle.setStyle("-fx-fill: #212121;");

    // Filtered clubs logic
    filteredClubs.setAll(allClubs);

    // GridPane for clubs
    GridPane clubsGrid = new GridPane();
    clubsGrid.setHgap(24);
    clubsGrid.setVgap(24);
    clubsGrid.setAlignment(Pos.CENTER);

    // Helper to update the grid
    Runnable showClubs = () -> {
        clubsGrid.getChildren().clear();
        int toShow = Math.min(3, filteredClubs.size());
        for (int i = 0; i < toShow; i++) {
            VBox clubCard = createClubCard(filteredClubs.get(i));
            clubsGrid.add(clubCard, i % 3, i / 3);
        }
    };
    showClubs.run();

    // "See More" button
    Button seeMoreClubsBtn = new Button("See More");
    styleMainButton(seeMoreClubsBtn);
    seeMoreClubsBtn.setVisible(filteredClubs.size() > 3);
    seeMoreClubsBtn.setOnAction(e -> {
        clubsGrid.getChildren().clear();
        for (int i = 0; i < filteredClubs.size(); i++) {
            VBox clubCard = createClubCard(filteredClubs.get(i));
            clubsGrid.add(clubCard, i % 3, i / 3);
        }
        seeMoreClubsBtn.setVisible(false);
    });

    // Search logic
    searchField.textProperty().addListener((obs, oldVal, newVal) -> {
        filteredClubs.setAll(allClubs.stream()
            .filter(club -> club.getName().toLowerCase().contains(newVal.toLowerCase()) ||
                            club.getType().toLowerCase().contains(newVal.toLowerCase()) ||
                            club.getDepartment().toLowerCase().contains(newVal.toLowerCase()))
            .collect(Collectors.toList()));
        seeMoreClubsBtn.setVisible(filteredClubs.size() > 3);
        showClubs.run();
    });

    clubsSection.getChildren().addAll(clubsTitle, clubsGrid, seeMoreClubsBtn);

    // --- Upcoming Events Section ---
    VBox eventsSection = new VBox(7);
    Text eventsTitle = new Text("Upcoming Events");
    eventsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
    eventsTitle.setStyle("-fx-fill: #212121;");

    GridPane eventsGrid = new GridPane();
    eventsGrid.setHgap(24);
    eventsGrid.setVgap(24);
    eventsGrid.setAlignment(Pos.CENTER);

    Runnable showEvents = () -> {
        eventsGrid.getChildren().clear();
        int toShow = Math.min(3, allEvents.size());
        for (int i = 0; i < toShow; i++) {
            VBox eventCard = createEventCard(allEvents.get(i));
            eventsGrid.add(eventCard, i % 3, i / 3);
        }
    };
    showEvents.run();

    Button seeMoreEventsBtn = new Button("See More");
    styleMainButton(seeMoreEventsBtn);
    seeMoreEventsBtn.setVisible(allEvents.size() > 3);
    seeMoreEventsBtn.setOnAction(e -> {
        eventsGrid.getChildren().clear();
        for (int i = 0; i < allEvents.size(); i++) {
            VBox eventCard = createEventCard(allEvents.get(i));
            eventsGrid.add(eventCard, i % 3, i / 3);
        }
        seeMoreEventsBtn.setVisible(false);
    });

    eventsSection.getChildren().addAll(eventsTitle, eventsGrid, seeMoreEventsBtn);

    // --- Why Use This System Section ---
    VBox whyBox = new VBox(8);
    whyBox.setPadding(new Insets(16));
    Text whyTitle = new Text("Why Use This System?");
    whyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    whyTitle.setStyle("-fx-fill: #212121;");
    ListView<String> whyList = new ListView<>();
    whyList.getItems().addAll(
            "One-click Club Join Request",
            "Event Registration System",
            "All Club Info in One Place",
            "Direct Social Links to Clubs");
    whyList.setMaxHeight(90);
    whyList.setStyle("-fx-background-color: #f8f9f8; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-background-radius: 10;");

    Button loginBtn = new Button("Login");
    Button signupBtn = new Button("Sign Up");
    styleMainButton(loginBtn);
    styleMainButton(signupBtn);
    HBox loginSignupBox = new HBox(18, loginBtn, signupBtn);
    loginSignupBox.setAlignment(Pos.CENTER);

    whyBox.getChildren().addAll(whyTitle, whyList, loginSignupBox);

    loginBtn.setOnAction(e -> showLoginUI());
    signupBtn.setOnAction(e -> showRegistrationUI());

    VBox mainContent = new VBox(18, headerBox, searchBox, clubsSection, eventsSection, whyBox);
    mainContent.setPadding(new Insets(28));
    mainContent.setAlignment(Pos.TOP_CENTER);
    mainContent.setStyle("-fx-background-color: #f6f6f3; -fx-border-radius: 18; -fx-background-radius: 18;");

    ScrollPane scroll = new ScrollPane(mainContent);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background:transparent;");

    mainUI = new VBox(scroll);

    // --- Admin Back Button in MenuBar ---
    if (loggedInUser != null && (loggedInRole.equals("SUPER_ADMIN") || loggedInRole.equals("DEPT_ADMIN"))) {
        menuBar.getMenus().removeIf(m -> m.getText().equals("Back to Admin Panel"));
        Menu backAdminMenu = new Menu("Back to Admin Panel");
        backAdminMenu.setStyle("-fx-text-fill: #fff; -fx-font-size: 18px;");
        MenuItem backItem = new MenuItem("Return");
        backItem.setOnAction(e -> {
            setupAdminUI();
            root.setTop(null);
            root.setLeft(adminSidebar);
            root.setCenter(adminMainContent);
        });
        backAdminMenu.getItems().add(backItem);
        int logoutIdx = menuBar.getMenus().size();
        for (int i = 0; i < menuBar.getMenus().size(); i++) {
            if (menuBar.getMenus().get(i).getText().equals("Logout")) {
                logoutIdx = i;
                break;
            }
        }
        menuBar.getMenus().add(logoutIdx, backAdminMenu);
    } else {
        menuBar.getMenus().removeIf(m -> m.getText().equals("Back to Admin Panel"));
    }
}

    private void updateClubCards(FlowPane clubsBox, ObservableList<Club> clubsToShow) {
    clubsBox.getChildren().clear();
    for (Club club : clubsToShow) {
        VBox clubCard = new VBox(8);
        clubCard.setPadding(new Insets(16));
        clubCard.setSpacing(6);
        clubCard.setStyle("-fx-background-color: #fff; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-background-radius: 12;");
        
        Text clubName = new Text(club.getName());
        clubName.setFont(Font.font("Arial", 18));
        clubName.setStyle("-fx-fill: #222;");
        clubName.setUnderline(true);

        clubName.setOnMouseClicked(e -> {
            String url = club.getWebsite();
            if (url != null && !url.isEmpty()) {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } catch (Exception ex) {
                    showAlert("Could not open website!");
                }
            } else {
                showAlert("No website link for this club.");
            }
        });

        Text shortInfo = new Text(club.getShortInfo());
        shortInfo.setFont(Font.font("Arial", 14));
        shortInfo.setStyle("-fx-fill: #555;");
        
        Button viewBtn = new Button("View");
        styleMainButton(viewBtn);
        viewBtn.setOnAction(e -> showClubDetails(club));
        
        Button joinBtn = new Button("Join Request");
        styleMainButton(joinBtn);
        joinBtn.setOnAction(e -> {
            userJoinedClubs.computeIfAbsent(loggedInUser, k -> new ArrayList<>()).add(club.getName());
            showAlert("Join request sent to " + club.getName() + "!");
        });

        clubCard.getChildren().addAll(clubName, shortInfo, new HBox(8, viewBtn, joinBtn));
        clubCard.setAlignment(Pos.CENTER);
        clubsBox.getChildren().add(clubCard);
    }
}
private VBox createClubCard(Club club) {
    VBox clubCard = new VBox(8);
    clubCard.setPadding(new Insets(16));
    clubCard.setSpacing(6);
    clubCard.setStyle("-fx-background-color: #fff; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-background-radius: 12;");
    Text clubName = new Text(club.getName());
    clubName.setFont(Font.font("Arial", 18));
    clubName.setStyle("-fx-fill: #222;");
    clubName.setUnderline(true);

    clubName.setOnMouseClicked(e -> {
        String url = club.getWebsite();
        if (url != null && !url.isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception ex) {
                showAlert("Could not open website!");
            }
        } else {
            showAlert("No website link for this club.");
        }
    });

    Text shortInfo = new Text(club.getShortInfo());
    shortInfo.setFont(Font.font("Arial", 14));
    shortInfo.setStyle("-fx-fill: #555;");

    Button viewBtn = new Button("View");
    styleMainButton(viewBtn);
    viewBtn.setOnAction(e -> showClubDetails(club));

    Button joinBtn = new Button("Join Request");
    styleMainButton(joinBtn);
    joinBtn.setOnAction(e -> {
        userJoinedClubs.computeIfAbsent(loggedInUser, k -> new ArrayList<>()).add(club.getName());
        showAlert("Join request sent to " + club.getName() + "!");
    });

    clubCard.getChildren().addAll(clubName, shortInfo, new HBox(8, viewBtn, joinBtn));
    clubCard.setAlignment(Pos.CENTER);
    return clubCard;
}

private VBox createEventCard(Event ev) {
    VBox eventCard = new VBox(8);
    eventCard.setPadding(new Insets(16));
    eventCard.setStyle("-fx-background-color: #fff; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-background-radius: 12;");
    Text eventTitle = new Text("Event: " + ev.title);
    eventTitle.setFont(Font.font("Arial", 16));
    Text eventDate = new Text("Date: " + ev.date);
    Button detailsBtn = new Button(ev.title.equals("Cultural Night") ? "Register" : "Details");
    styleMainButton(detailsBtn);
    detailsBtn.setOnAction(e -> showEventDetails(ev));
    eventCard.getChildren().addAll(eventTitle, eventDate, detailsBtn);
    return eventCard;
}


    private void styleMainButton(Button btn) {
        btn.setStyle("-fx-background-color: #19706a; -fx-text-fill: #fff; -fx-font-size: 16px; -fx-background-radius: 8; -fx-border-radius: 8;");
        btn.setPrefWidth(130);
        btn.setPrefHeight(40);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #14514c; -fx-text-fill: #fff; -fx-font-size: 16px; -fx-background-radius: 8; -fx-border-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #19706a; -fx-text-fill: #fff; -fx-font-size: 16px; -fx-background-radius: 8; -fx-border-radius: 8;"));
    }

    private void setupLoginUI() {
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(50));
        box.setStyle("-fx-background-color: #f9f9f8; -fx-border-color: #19706a; -fx-border-radius: 14; -fx-background-radius: 14;");

        ImageView logo;
        try {
            logo = new ImageView(new Image(getClass().getResourceAsStream("/test/photo_2025-04-19_11-58-22.jpg")));
        } catch (Exception e) {
            logo = new ImageView();
        }
        logo.setFitHeight(110);
        logo.setFitWidth(110);

        Text title = new Text("Login");
        title.setFont(Font.font("Arial", 28));
        title.setStyle("-fx-fill: #19706a;");

        Label userLabel = new Label("Username");
        TextField userField = new TextField();
        userField.setPromptText("Enter your username");
        userField.setPrefWidth(220);
        userField.setFont(Font.font("Arial", 16));

        Label passLabel = new Label("Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");
        passField.setPrefWidth(220);
        passField.setFont(Font.font("Arial", 16));

        Button loginBtn = new Button("Login");
        styleMainButton(loginBtn);
        Label msg = new Label();
        msg.setTextFill(Color.RED);

        userField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) passField.requestFocus();
        });
        passField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) loginBtn.fire();
        });

        loginBtn.setOnAction(e -> {
            String u = userField.getText().trim();
            String p = passField.getText();
            if (userPass.containsKey(u) && userPass.get(u).equals(p)) {
                loggedInUser = u;
                loggedInRole = userRole.get(u);
                if (loggedInRole != null && (loggedInRole.startsWith("SUPER") || loggedInRole.startsWith("DEPT") || loggedInRole.startsWith("MODERATOR"))) {
                    showAdminUI();
                } else {
                    showWelcomeWindow(u);
                }
            } else {
                msg.setText("Invalid username or password!");
            }
        });

        Button toReg = new Button("No account? Register");
        toReg.setStyle("-fx-background-color: transparent; -fx-text-fill: #19706a; -fx-underline: true;");
        toReg.setOnAction(e -> showRegistrationUI());

        box.getChildren().addAll(logo, title, userLabel, userField, passLabel, passField, loginBtn, msg, toReg);
        loginUI = new VBox(box);
        loginUI.setAlignment(Pos.CENTER);
    }

    private void setupRegistrationUI() {
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(50));
        box.setStyle("-fx-background-color: #f9f9f8; -fx-border-color: #19706a; -fx-border-radius: 14; -fx-background-radius: 14;");

        ImageView logo;
        try {
            logo = new ImageView(new Image(getClass().getResourceAsStream("/test/photo_2025-04-19_11-58-22.jpg")));
        } catch (Exception e) {
            logo = new ImageView();
        }
        logo.setFitHeight(110);
        logo.setFitWidth(110);

        Text title = new Text("Sign Up");
        title.setFont(Font.font("Arial", 28));
        title.setStyle("-fx-fill: #19706a;");

        Label userLabel = new Label("Choose Username");
        TextField userField = new TextField();
        userField.setPromptText("Enter a username");
        userField.setPrefWidth(220);
        userField.setFont(Font.font("Arial", 16));

        Label passLabel = new Label("Choose Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter a password");
        passField.setPrefWidth(220);
        passField.setFont(Font.font("Arial", 16));

        Button regBtn = new Button("Register");
        styleMainButton(regBtn);
        Label msg = new Label();
        msg.setTextFill(Color.RED);

        userField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) passField.requestFocus();
        });
        passField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) regBtn.fire();
        });

        regBtn.setOnAction(e -> {
            String u = userField.getText().trim();
            String p = passField.getText();
            if (u.isEmpty() || p.isEmpty()) {
                msg.setText("Username and password required!");
            } else if (userPass.containsKey(u)) {
                msg.setText("Username already exists!");
            } else {
                userPass.put(u, p);
                userRole.put(u, "USER");
                msg.setTextFill(Color.GREEN);
                msg.setText("Registration successful! Please login.");
            }
        });

        Button toLogin = new Button("Already have account? Login");
        toLogin.setStyle("-fx-background-color: transparent; -fx-text-fill: #19706a; -fx-underline: true;");
        toLogin.setOnAction(e -> showLoginUI());

        box.getChildren().addAll(logo, title, userLabel, userField, passLabel, passField, regBtn, msg, toLogin);
        regUI = new VBox(box);
        regUI.setAlignment(Pos.CENTER);
    }

    private void showWelcomeWindow(String username) {
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: linear-gradient(to bottom right, #19706a, #4ecdc4);");

        Text welcome = new Text("Welcome, " + username + "!");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        welcome.setFill(Color.WHITE);

        Text systemTitle = new Text("BAUST Club Management System");
        systemTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        systemTitle.setFill(Color.WHITE);

        Text journey = new Text("Your journey to club activities begins here");
        journey.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        journey.setFill(Color.rgb(255,255,255,0.9));

        box.getChildren().addAll(welcome, systemTitle, journey);

        root.setTop(null);
        root.setLeft(null);
        root.setCenter(box);
        root.setBottom(null);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> showMainUI());
        pause.play();
    }

    private void showMainUI() {
        setupMainUI();
        root.setTop(menuBar);
        root.setLeft(null);
        root.setCenter(mainUI);
        root.setBottom(footer);
    }
    private void showLoginUI() {
        setupLoginUI();
        root.setTop(null);
        root.setLeft(null);
        root.setCenter(loginUI);
        root.setBottom(null);
    }
    private void showRegistrationUI() {
        setupRegistrationUI();
        root.setTop(null);
        root.setLeft(null);
        root.setCenter(regUI);
        root.setBottom(null);
    }
    private void showAdminUI() {
        setupAdminUI();
        root.setTop(null);
        root.setLeft(adminSidebar);
        ScrollPane scroll = new ScrollPane(adminMainContent);
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        root.setBottom(null);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    private void showClubDetails(Club club) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(club.getName() + " Details");
        alert.setHeaderText(club.getName());
        alert.setContentText("Type: " + club.getType() + "\nDepartment: " + club.getDepartment() +
                "\nInfo: " + club.getShortInfo() + "\nModerator: " + club.moderator);
        alert.showAndWait();
    }
    private void showEventDetails(Event ev) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ev.title + " Details");
        alert.setHeaderText(ev.title);
        alert.setContentText("Club: " + ev.clubName + "\nDate: " + ev.date + "\nDepartment: " + ev.department + "\n" + ev.description);
        alert.showAndWait();
    }
    private void showContactDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Us");
        alert.setHeaderText("Contact Us");
        alert.setContentText("Email: hasibulhasibofficial@gmail.com\nPhone: 01737106285");
        alert.showAndWait();
    }
    private void showAboutDevelopers() {
        VBox aboutBox = new VBox(18);
        aboutBox.setAlignment(Pos.CENTER);
        aboutBox.setPadding(new Insets(40));
        Text title = new Text("About the Developers");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setStyle("-fx-fill: #1a1a1a;");
        Separator sep = new Separator();

        HBox devs = new HBox(40);
        devs.setAlignment(Pos.CENTER);

        devs.getChildren().addAll(
                makeDevCard("Hasibul Hasib"),
                makeDevCard("Mst.Jugnu Khatun"),
                makeDevCard("Most.Arifa Akter ALLo")
         
        );
        aboutBox.getChildren().addAll(title, sep, devs);

        Stage dialog = new Stage();
        Scene aboutScene = new Scene(aboutBox, 800, 400);
        dialog.setTitle("About the Developers");
        dialog.setScene(aboutScene);
        dialog.show();
    }
    private VBox makeDevCard(String name) {
        VBox card = new VBox(7);
        card.setAlignment(Pos.CENTER);
        Circle pic = new Circle(48, Color.LIGHTGRAY);
        Text nameT = new Text(name);
        nameT.setFont(Font.font("Arial", FontWeight.BOLD, 19));
        nameT.setStyle("-fx-fill: #1a1a1a;");
        Text role = new Text("Developer");
        role.setFont(Font.font("Arial", 14));
        role.setStyle("-fx-fill: #444;");
        Text msg = new Text("Pushing B.Sc Engg in CSE");
        msg.setFont(Font.font("Arial", 13));
        msg.setStyle("-fx-fill: #888;");
        card.getChildren().addAll(pic, nameT, role, msg);
        return card;
    }

    
    private void showProfilePage() {
    Stage profileStage = new Stage();
    profileStage.setTitle("My Profile");

    VBox box = new VBox(18);
    box.setPadding(new Insets(32));
    box.setAlignment(Pos.TOP_LEFT);

    // Title
    Text title = new Text("My Profile");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
    title.setFill(Color.web("#19706a"));
    box.getChildren().add(title);

    // Username
    Text userText = new Text("Username: " + (loggedInUser == null ? "N/A" : loggedInUser));
    userText.setFont(Font.font("Arial", 18));
    box.getChildren().add(userText);

    // Password (masked)
    String userPassword = userPass.getOrDefault(loggedInUser, "N/A");
    String masked = userPassword.replaceAll(".", "*");
    Text passText = new Text("Password: " + masked);
    passText.setFont(Font.font("Arial", 18));
    box.getChildren().add(passText);

    // Joined Clubs
    Text clubsTitle = new Text("Joined Clubs:");
    clubsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    box.getChildren().add(clubsTitle);

    List<String> joinedClubs = userJoinedClubs.getOrDefault(loggedInUser, new ArrayList<>());
    if (joinedClubs.isEmpty()) {
        Text none = new Text("No joined clubs!");
        none.setFont(Font.font("Arial", 16));
        box.getChildren().add(none);
    } else {
        for (String clubName : joinedClubs) {
            Text club = new Text("• " + clubName);
            club.setFont(Font.font("Arial", 16));
            box.getChildren().add(club);
        }
    }

    Scene s = new Scene(box, 400, 350);
    profileStage.setScene(s);
    profileStage.show();
}

//showAlert("My Profile Page \nName: " + (loggedInUser != null ? loggedInUser : "User") + "\nJoined Clubs: Tech Club");
//showAlert("My Profile Page \nName: " + (loggedInUser != null ? loggedInUser : "User") + "\nJoined Clubs: Tech Club");
    
    
   private void showHelpPage() {
        showAlert("Help / Contact Page \nEmail: hasibulhasibofficial@gmail.com\nFAQs:\n- How to join?\n- How to register for events?");
    }
    // ADMIN dashboard here ------------------------------------------------------------------------>>>>>>>

private void setupAdminUI() {
    adminSidebar = new VBox(20);
    adminSidebar.setPadding(new Insets(36, 12, 36, 24));
    adminSidebar.setStyle("-fx-background-color: #374151; -fx-background-radius: 16 0 0 16;");
    adminSidebar.setPrefWidth(230);

    Label adminLabel = new Label("Admin Panel");
    adminLabel.setFont(Font.font("Arial", 24));
    adminLabel.setStyle("-fx-text-fill: #fff;");

    Button dashBtn = makeSidebarBtn("Dashboard", true);
    Button deptBtn = makeSidebarBtn("Departments", false);
    Button clubsBtn = makeSidebarBtn("Clubs", false);
    Button usersBtn = makeSidebarBtn("Users", false);
    Button eventsBtn = makeSidebarBtn("Events", false);
    Button viewSiteBtn = makeSidebarBtn("View Site", false);
    Button logoutBtn = makeSidebarBtn("Logout", false);

    adminSidebar.getChildren().addAll(adminLabel, dashBtn, deptBtn, clubsBtn, usersBtn, eventsBtn, viewSiteBtn, logoutBtn);

    // --- Dashboard UI ---
    VBox dashMain = new VBox(24);
    dashMain.setPadding(new Insets(36));
    dashMain.setStyle("-fx-background-color: #fff; -fx-background-radius: 0 16 16 0;");

    Text dashTitle = new Text("Dashboard");
    dashTitle.setFont(Font.font("Arial", 30));
    dashTitle.setStyle("-fx-fill: #212121;");

    HBox stats = new HBox(28);
    stats.setAlignment(Pos.CENTER_LEFT);

    Button deptCard = makeCardButton("Departments", "Clubs", departments.size());
    Button clubCard = makeCardButton("Clubs", "Clubs", allClubs.size());
    Button userCard = makeCardButton("Users", "Users", userPass.size());
    Button eventCard = makeCardButton("Events", "Events", allEvents.size());

    stats.getChildren().addAll(deptCard, clubCard, userCard, eventCard);

//    Text tableTitle = new Text("Club Moderators");
//    tableTitle.setFont(Font.font("Arial", 22));
//    TableView<Club> table = new TableView<>();
//    TableColumn<Club, String> nameCol = new TableColumn<>("Name");
//    TableColumn<Club, String> clubCol = new TableColumn<>("Club");
//    TableColumn<Club, String> deptCol = new TableColumn<>("Department");
//    nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().moderator));
//    clubCol.setCellValueFactory(new PropertyValueFactory<>("name"));
//    deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
//    table.getColumns().addAll(nameCol, clubCol, deptCol);
//    table.getItems().addAll(allClubs);
//    table.setPrefHeight(130);
//    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


// --- Table Title & Table ---
Text tableTitle = new Text("Club Moderators");
tableTitle.setFont(Font.font("Arial", 22));

TableView<Club> table = new TableView<>();
table.setPrefWidth(520);

TableColumn<Club, String> moderatorCol = new TableColumn<>("Moderator");
moderatorCol.setCellValueFactory(new PropertyValueFactory<>("moderator"));
moderatorCol.setPrefWidth(120);

TableColumn<Club, String> clubCol = new TableColumn<>("Club");
clubCol.setCellValueFactory(new PropertyValueFactory<>("name"));
clubCol.setPrefWidth(230);

TableColumn<Club, String> deptCol = new TableColumn<>("Department");
deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
deptCol.setPrefWidth(100);

table.getColumns().setAll(moderatorCol, clubCol, deptCol);
table.setItems(allClubs);
table.setPrefHeight(180);
table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

VBox tableBox = new VBox(8, tableTitle, table);

// তারপর layout-এ ব্যবহার করো
//HBox content = new HBox(36, tableBox, manageBox); // manageBox 

    // --- CHANGED SECTION STARTS HERE ---
    VBox manageClubsBox = new VBox(8);
    Label clubsLabel = new Label("Manage Clubs");
    clubsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    Button addClubBtn = new Button("Add Club");
    Button editDeleteClubsBtn = new Button("Edit/Delete Clubs");
    for (Button b : Arrays.asList(addClubBtn, editDeleteClubsBtn)) {
        b.setStyle("-fx-background-color: #374151; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;");
        b.setPrefWidth(190);
        b.setPrefHeight(42);
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #19706a; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #374151; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"));
    }
    addClubBtn.setOnAction(e -> showAddClubDialog());
    editDeleteClubsBtn.setOnAction(e -> showEditClubDialog());
    manageClubsBox.getChildren().addAll(clubsLabel, addClubBtn, editDeleteClubsBtn);

    VBox manageEventsBox = new VBox(8);
    Label eventsLabel = new Label("Manage Events");
    eventsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    Button addEventBtn = new Button("Add Event");
    Button editDeleteEventsBtn = new Button("Edit/Delete Events");
    for (Button b : Arrays.asList(addEventBtn, editDeleteEventsBtn)) {
        b.setStyle("-fx-background-color: #374151; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;");
        b.setPrefWidth(190);
        b.setPrefHeight(42);
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #19706a; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #374151; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"));
    }
    addEventBtn.setOnAction(e -> showAddEventDialog());
    editDeleteEventsBtn.setOnAction(e -> showEditEventDialog());
    manageEventsBox.getChildren().addAll(eventsLabel, addEventBtn, editDeleteEventsBtn);

    Button approveUsersBtn = new Button("Approve Users");
    Button postAnnBtn = new Button("Post Announcement");
    for (Button b : Arrays.asList(approveUsersBtn, postAnnBtn)) {
        b.setStyle("-fx-background-color: #374151; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;");
        b.setPrefWidth(190);
        b.setPrefHeight(42);
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #19706a; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #374151; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"));
    }
    approveUsersBtn.setOnAction(e -> showAlert("Approve Users list!"));
    postAnnBtn.setOnAction(e -> showAlert("Announcement form!"));

    VBox manageBox = new VBox(18, manageClubsBox, manageEventsBox, approveUsersBtn, postAnnBtn);
    // --- CHANGED SECTION ENDS HERE ---

    HBox content = new HBox(36,
            new VBox(tableTitle, table),
            manageBox
    );
    dashMain.getChildren().addAll(dashTitle, stats, content);

    adminMainContent = new VBox(dashMain);

    // --- Set sidebar button actions ---
    dashBtn.setOnAction(e -> {
        root.setTop(null);
        root.setLeft(adminSidebar);
        root.setCenter(adminMainContent);
        root.setBottom(null);
    });
    deptBtn.setOnAction(e -> showDepartmentsPage());
    clubsBtn.setOnAction(e -> showClubsPage("all", null));
    usersBtn.setOnAction(e -> showUsersPage()); // if you have this
    eventsBtn.setOnAction(e -> showEventsPage());
    viewSiteBtn.setOnAction(e -> showMainUI());
    logoutBtn.setOnAction(e -> {
        loggedInUser = null;
        loggedInRole = null;
        root.setTop(null);
        root.setLeft(null);
        root.setCenter(null);
        root.setBottom(null);
        showLoginUI();
    });

    // --- Set default view: dashboard ---
    root.setTop(null); // Hide the top bar for admin panel
    root.setLeft(adminSidebar);
    root.setCenter(adminMainContent); // Shows the dashboard by default
    root.setBottom(null); // (Optional) hide footer
}

   
   
///implement method ---------------->>>>>>>>>>
private void showDepartmentsPage() {
    Text title = new Text("Departments");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
    title.setStyle("-fx-fill: #212121;");

    ListView<String> deptList = new ListView<>();
    deptList.setItems(FXCollections.observableArrayList(departments));
    deptList.setStyle("-fx-font-size: 17px; -fx-background-radius: 10; -fx-padding: 6;");

    deptList.setPrefHeight(Math.min(departments.size() * 48, 400)); // Auto-size, max 400px

    VBox deptBox = new VBox(18, title, deptList);
    deptBox.setPadding(new Insets(36, 32, 36, 32));
    deptBox.setStyle("-fx-background-color: #fff; -fx-background-radius: 16;");

    ScrollPane scroll = new ScrollPane(deptBox);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background: transparent;");

    root.setCenter(scroll);
}

private void showClubsPage(String filterType, String filterValue) {
    Text title = new Text("Clubs");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
    title.setStyle("-fx-fill: #212121;");

    TableView<Club> clubTable = new TableView<>(allClubs);
    TableColumn<Club, String> nameCol = new TableColumn<>("Name");
    TableColumn<Club, String> typeCol = new TableColumn<>("Type");
    TableColumn<Club, String> deptCol = new TableColumn<>("Department");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
    clubTable.getColumns().addAll(nameCol, typeCol, deptCol);
    clubTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    clubTable.setPrefHeight(Math.min(allClubs.size() * 48 + 40, 400));

    VBox clubsBox = new VBox(18, title, clubTable);
    clubsBox.setPadding(new Insets(36, 32, 36, 32));
    clubsBox.setStyle("-fx-background-color: #fff; -fx-background-radius: 16;");

    ScrollPane scroll = new ScrollPane(clubsBox);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background: transparent;");

    root.setCenter(scroll);
}


private void showEventsPage() {
    Text title = new Text("Events");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
    title.setStyle("-fx-fill: #212121;");

    TableView<Event> eventTable = new TableView<>(allEvents);

    TableColumn<Event, String> nameCol = new TableColumn<>("Title");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("title"));

    TableColumn<Event, String> clubCol = new TableColumn<>("Club");
    clubCol.setCellValueFactory(new PropertyValueFactory<>("clubName")); // <-- শুধু এইটা ঠিক করো

    TableColumn<Event, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

    eventTable.getColumns().addAll(nameCol, clubCol, dateCol);
    eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    eventTable.setPrefHeight(Math.min(allEvents.size() * 48 + 40, 400));

    VBox eventsBox = new VBox(18, title, eventTable);
    eventsBox.setPadding(new Insets(36, 32, 36, 32));
    eventsBox.setStyle("-fx-background-color: #fff; -fx-background-radius: 16;");

    ScrollPane scroll = new ScrollPane(eventsBox);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background: transparent;");

    root.setCenter(scroll);
}

private void showUsersPage() {
    Text title = new Text("Users");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
    title.setStyle("-fx-fill: #212121;");

    ListView<String> usersList = new ListView<>();
    usersList.setItems(FXCollections.observableArrayList(userPass.keySet()));
    usersList.setStyle("-fx-font-size: 17px; -fx-background-radius: 10; -fx-padding: 6;");
    usersList.setPrefHeight(Math.min(userPass.size() * 48, 400));

    VBox usersBox = new VBox(18, title, usersList);
    usersBox.setPadding(new Insets(36, 32, 36, 32));
    usersBox.setStyle("-fx-background-color: #fff; -fx-background-radius: 16;");

    ScrollPane scroll = new ScrollPane(usersBox);
    scroll.setFitToWidth(true);
    scroll.setStyle("-fx-background: transparent;");

    root.setCenter(scroll);
}

    
    private Button makeSidebarBtn(String text, boolean selected) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(selected
                ? "-fx-background-color: #222c2f; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"
                : "-fx-background-color: transparent; -fx-text-fill: #fff; -fx-font-size: 17px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #19706a; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle(selected
                ? "-fx-background-color: #222c2f; -fx-text-fill: #fff; -fx-font-size: 17px; -fx-background-radius: 8;"
                : "-fx-background-color: transparent; -fx-text-fill: #fff; -fx-font-size: 17px;"));
        return btn;
    }
    private Button makeCardButton(String label, String sub, int count) {
        VBox card = new VBox(2);
        Text bigT = new Text(count + "");
        bigT.setFont(Font.font("Arial", 24));
        bigT.setStyle("-fx-fill: #19706a;");
        Text smallT = new Text(label);
        smallT.setFont(Font.font("Arial", 14));
        smallT.setStyle("-fx-fill: #444;");
        card.getChildren().addAll(bigT, smallT);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #f8f9f8; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-background-radius: 12;");
        Button btn = new Button("", card);
        btn.setStyle("-fx-background-color: transparent;");
        btn.setPrefWidth(170);
        btn.setPrefHeight(70);
        return btn;
    }
    private void showAdminDashboard() {
    VBox rootBox = new VBox(20);
    rootBox.setPadding(new Insets(20));

    // Clubs Section
    Text clubsTitle = new Text("Manage Clubs");
    clubsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    Button addClubBtn = new Button("Add Club");
    Button editClubBtn = new Button("Edit/Delete Clubs");
    HBox clubsButtons = new HBox(10, addClubBtn, editClubBtn);

    // Events Section
    Text eventsTitle = new Text("Manage Events");
    eventsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    Button addEventBtn = new Button("Add Event");
    Button editEventBtn = new Button("Edit/Delete Events");
    HBox eventsButtons = new HBox(10, addEventBtn, editEventBtn);

    rootBox.getChildren().addAll(clubsTitle, clubsButtons, eventsTitle, eventsButtons);

    // Action handlers
    addClubBtn.setOnAction(e -> showAddClubDialog());
    editClubBtn.setOnAction(e -> showEditClubDialog());

    addEventBtn.setOnAction(e -> showAddEventDialog());
    editEventBtn.setOnAction(e -> showEditEventDialog());

    root.setCenter(rootBox);
}
 
    private void showAddClubDialog() {
    Stage dialog = new Stage();
    dialog.setTitle("Add New Club");

    VBox box = new VBox(10);
    box.setPadding(new Insets(20));

    TextField nameField = new TextField();
    nameField.setPromptText("Club Name");
    TextField typeField = new TextField();
    typeField.setPromptText("Type (Technical/Cultural/Sports)");
    TextField deptField = new TextField();
    deptField.setPromptText("Department");
    TextField websiteField = new TextField();
    websiteField.setPromptText("Website URL");
    TextField shortInfoField = new TextField();
    shortInfoField.setPromptText("Short Info");
    TextField moderatorField = new TextField();
    moderatorField.setPromptText("Moderator");

    Button addBtn = new Button("Add Club");
    addBtn.setOnAction(e -> {
        Club newClub = new Club(
            nameField.getText(),
            typeField.getText(),
            deptField.getText(),
            shortInfoField.getText(),
            moderatorField.getText(),
            websiteField.getText()
        );
        allClubs.add(newClub);
        filteredClubs.add(newClub);
        saveClubsToJson(); // ← এই লাইনটা যোগ করো (এটাই মূল পরিবর্তন)
        dialog.close();
    });

    box.getChildren().addAll(
        new Label("Add New Club"),
        nameField, typeField, deptField, websiteField, shortInfoField, moderatorField, addBtn
    );

    Scene s = new Scene(box, 400, 400);
    dialog.setScene(s);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.showAndWait();
}

private void showEditClubDialog() {
    Stage dialog = new Stage();
    dialog.setTitle("Edit/Delete Clubs");

    VBox box = new VBox(12);
    box.setPadding(new Insets(20));

    TableView<Club> table = new TableView<>(allClubs);
    TableColumn<Club, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
    TableColumn<Club, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
    TableColumn<Club, String> deptCol = new TableColumn<>("Department");
    deptCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
    table.getColumns().addAll(nameCol, typeCol, deptCol);
    table.setPrefHeight(200);

    Button editBtn = new Button("Edit Selected");
    Button deleteBtn = new Button("Delete Selected");

    editBtn.setOnAction(e -> {
        Club selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        showEditClubFieldsDialog(selected, table);
    });

    deleteBtn.setOnAction(e -> {
        Club selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        allClubs.remove(selected);
        filteredClubs.remove(selected);
        saveClubsToJson(); // ← এই লাইনটা যোগ করো (এটাই মূল পরিবর্তন)

        
        // saveClubsToFile(); // যদি ফাইল সেভ করো
    });

    box.getChildren().addAll(new Label("Edit/Delete Clubs"), table, new HBox(10, editBtn, deleteBtn));
    Scene s = new Scene(box, 500, 350);
    dialog.setScene(s);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.showAndWait();
}


private void showEditClubFieldsDialog(Club club, TableView<Club> table) {
    Stage dialog = new Stage();
    dialog.setTitle("Edit Club");

    VBox box = new VBox(10);
    box.setPadding(new Insets(20));

    TextField nameField = new TextField(club.getName());
    TextField typeField = new TextField(club.getType());
    TextField deptField = new TextField(club.getDepartment());
    TextField websiteField = new TextField(club.getWebsite());
    TextField shortInfoField = new TextField(club.getShortInfo());
    TextField moderatorField = new TextField(club.getModerator());

    Button saveBtn = new Button("Save Changes");
    saveBtn.setOnAction(e -> {
        // Update the club object
        club.setName(nameField.getText());
        club.setType(typeField.getText());
        club.setDepartment(deptField.getText());
        club.setWebsite(websiteField.getText());
        club.setShortInfo(shortInfoField.getText());
        club.setModerator(moderatorField.getText());

        // --- THIS IS THE IMPORTANT PART ---
        int idx = allClubs.indexOf(club);
        if (idx >= 0) {
            allClubs.set(idx, club);
        }

        if (table != null) table.refresh();
        saveClubsToJson();
        dialog.close();
    });

    box.getChildren().addAll(
        new Label("Edit Club Info"),
        nameField, typeField, deptField, websiteField, shortInfoField, moderatorField, saveBtn
    );

    Scene s1 = new Scene(box, 400, 350);
    dialog.setScene(s1);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.showAndWait();
}



private void showAddEventDialog() {
    Stage dialog = new Stage();
    dialog.setTitle("Add New Event");

    VBox box = new VBox(10);
    box.setPadding(new Insets(20));

    TextField titleField = new TextField();
    titleField.setPromptText("Event Title");
    TextField clubField = new TextField();
    clubField.setPromptText("Club Name");
    TextField dateField = new TextField();
    dateField.setPromptText("Date");
    TextField deptField = new TextField();
    deptField.setPromptText("Department");
    TextField descField = new TextField();
    descField.setPromptText("Description");

    Button addBtn = new Button("Add Event");
    addBtn.setOnAction(e -> {
        Event newEvent = new Event(
            titleField.getText(),
            clubField.getText(),
            dateField.getText(),
            deptField.getText(),
            descField.getText(), "https://www.example.com/csefest2025"
        );
        allEvents.add(newEvent);
        dialog.close();
        saveClubsToJson(); // ← এই লাইনটা যোগ করো (এটাই মূল পরিবর্তন)

        // saveEventsToFile(); // যদি ফাইল সেভ করো
    });

    box.getChildren().addAll(new Label("Add New Event"), titleField, clubField, dateField, deptField, descField, addBtn);

    Scene s = new Scene(box, 400, 350);
    dialog.setScene(s);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.showAndWait();
}
private void showEditEventDialog() {
    Stage dialog = new Stage();
    dialog.setTitle("Edit/Delete Events");

    VBox box = new VBox(12);
    box.setPadding(new Insets(20));

    TableView<Event> table = new TableView<>(allEvents);
    TableColumn<Event, String> titleCol = new TableColumn<>("Title");
    titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().title));
    TableColumn<Event, String> clubCol = new TableColumn<>("Club");
    clubCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().clubName));
    TableColumn<Event, String> dateCol = new TableColumn<>("Date");
    
    
    dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateString()));
    // অথবা, যদি ফরম্যাটেড তারিখ চাও:


    dateCol.setCellValueFactory(data -> new SimpleStringProperty(
    data.getValue().getDate().format(DateTimeFormatter.ofPattern("d MMMM"))
));
    table.getColumns().addAll(titleCol, clubCol, dateCol);
    table.setPrefHeight(200);

    Button editBtn = new Button("Edit Selected");
    Button deleteBtn = new Button("Delete Selected");

    editBtn.setOnAction(e -> {
        Event selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        showEditEventFieldsDialog(selected, table);
    });

    deleteBtn.setOnAction(e -> {
        Event selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        allEvents.remove(selected);
        saveClubsToJson(); // ← এই লাইনটা যোগ করো (এটাই মূল পরিবর্তন)

        // saveEventsToFile(); // যদি ফাইল সেভ করো
    });

    box.getChildren().addAll(new Label("Edit/Delete Events"), table, new HBox(10, editBtn, deleteBtn));
    Scene scene = new Scene(box, 500, 350);
    dialog.setScene(scene);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.showAndWait();
}

// Helper: ইভেন্ট Edit করার জন্য ফিল্ডসহ ডায়ালগ
private void showEditEventFieldsDialog(Event event, TableView<Event> table) {
    Stage dialog = new Stage();
    dialog.setTitle("Edit Event");

    VBox box = new VBox(10);
    box.setPadding(new Insets(20));

    TextField titleField = new TextField(event.title);
    TextField clubField = new TextField(event.clubName);
TextField dateField = new TextField(
    event.getDate().format(DateTimeFormatter.ofPattern("d MMMM"))
);
    TextField deptField = new TextField(event.department);
    TextField descField = new TextField(event.description);

    Button saveBtn = new Button("Save Changes");
    saveBtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            event.title = titleField.getText();
            event.clubName = clubField.getText();
            event.setDateFromString(dateField.getText());
            event.department = deptField.getText();
            event.description = descField.getText();
            table.refresh();
            saveClubsToJson(); // ← এই লাইনটা যোগ করো (এটাই মূল পরিবর্তন)

            // saveEventsToFile(); // যদি ফাইল সেভ করো
            dialog.close();
        }
    });

    box.getChildren().addAll(
        new Label("Edit Event Info"),
        titleField, clubField, dateField, deptField, descField, saveBtn
    );

    Scene scene = new Scene(box, 400, 350);
    dialog.setScene(scene);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.showAndWait();
}

    private List<Event> getSortedEvents() {
    // Create a new list from allEvents to avoid modifying the original list
    List<Event> sortedEvents = new ArrayList<>(allEvents);

    // Sort by date (assuming date is in a comparable string format)
    sortedEvents.sort(Comparator.comparing(event -> event.date));

    return sortedEvents;
}










    public static class Club {
    private final String name;
    private final String type;
    private final String department;

    private final String shortInfo;
    private final String moderator;
    private final String website; // নতুন ফিল্ড

    public Club(String name, String type, String department,String shortInfo, String moderator, String website) {
        this.name = name;
        this.type = type;
        this.department = department;
        this.shortInfo = shortInfo;
        this.moderator = moderator;
        this.website = website;
    }

    // Getter methods
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDepartment() { return department; }
    public String getShortInfo() { return shortInfo; }
    public String getModerator() { return moderator; }
    public String getWebsite() { return website; }


        private void setName(String text) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void setType(String text) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void setDepartment(String text) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void setWebsite(String text) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void setShortInfo(String text) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void setModerator(String text) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    


//public static class Event {
//    private String title;
//    private String clubName;
//    private String dateString;      // For display, e.g. "25th August"
//    private LocalDate date;         // For sorting
//    private String department;
//    private String description;
//
//    // Constructor: parses the dateString to LocalDate if possible
//    public Event(String title, String clubName, String dateString, String department, String description) {
//        this.title = title;
//        this.clubName = clubName;
//        this.dateString = dateString;
//        this.department = department;
//        this.description = description;
//        this.date = parseDate(dateString);
//    }
//
//    // Helper to parse "25th August" or "September" etc.
//   private LocalDate parseDate(String dateString) {
//    int currentYear = LocalDate.now().getYear();
//
//    // 1. Try dd/MM/yyyy (e.g. 28/05/2025)
//    try {
//        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        return LocalDate.parse(dateString, fmt);
//    } catch (DateTimeParseException e) {
//        // 2. Try yyyy/MM/dd (e.g. 2025/05/28)
//        try {
//            DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//            return LocalDate.parse(dateString, fmt2);
//        } catch (DateTimeParseException e2) {
//            // 3. Try d['st']['nd']['rd']['th'] MMMM (e.g. 25th August)
//            try {
//                DateTimeFormatter fmt3 = new DateTimeFormatterBuilder()
//                    .parseCaseInsensitive()
//                    .appendPattern("d['st']['nd']['rd']['th'] MMMM")
//                    .parseDefaulting(ChronoField.YEAR, currentYear)
//                    .toFormatter();
//                return LocalDate.parse(dateString, fmt3);
//            } catch (DateTimeParseException e3) {
//                // 4. Try MMMM (e.g. September)
//                try {
//                    DateTimeFormatter fmt4 = new DateTimeFormatterBuilder()
//                        .parseCaseInsensitive()
//                        .appendPattern("MMMM")
//                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
//                        .parseDefaulting(ChronoField.YEAR, currentYear)
//                        .toFormatter();
//                    return LocalDate.parse(dateString, fmt4);
//                } catch (DateTimeParseException e4) {
//                    // If parsing fails, return a far-future date so it appears last
//                    return LocalDate.of(2025, 12, 31);
//                }
//            }
//        }
//    }
//}
//
//    public String getTitle()          { return title; }
//    public String getClubName()       { return clubName; }
//    public String getDateString()     { return dateString; }
//    public LocalDate getDate()        { return date; }
//    public String getDepartment()     { return department; }
//    public String getDescription()    { return description; }
//
//    // Returns true if the event is today or in the future
//    public boolean isUpcoming() {
//        return !date.isBefore(LocalDate.now());
//    }
//
//    // If you want to allow updating the date from a string (e.g. from a TextField)
//    public void setDateFromString(String newDateString) {
//        this.dateString = newDateString;
//        this.date = parseDate(newDateString);
//    }
//}
//
//   
    public static class Event {
    private String title;
    private String clubName;
    private String dateString;      // For display, e.g. "25th August"
    private LocalDate date;         // For sorting
    private String department;
    private String description;
    private String link;            // <-- Add this field

    // Constructor: parses the dateString to LocalDate if possible
    public Event(String title, String clubName, String dateString, String department, String description, String httpswwwexamplecomcsefest2025) {
        this.title = title;
        this.clubName = clubName;
        this.dateString = dateString;
        this.department = department;
        this.description = description;
        this.link = link;           // <-- Assign link
        this.date = parseDate(dateString);
    }

    // Helper to parse "25th August" or "September" etc.
    private LocalDate parseDate(String dateString) {
        int currentYear = LocalDate.now().getYear();

        // 1. Try dd/MM/yyyy (e.g. 28/05/2025)
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateString, fmt);
        } catch (DateTimeParseException e) {
            // 2. Try yyyy/MM/dd (e.g. 2025/05/28)
            try {
                DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                return LocalDate.parse(dateString, fmt2);
            } catch (DateTimeParseException e2) {
                // 3. Try d['st']['nd']['rd']['th'] MMMM (e.g. 25th August)
                try {
                    DateTimeFormatter fmt3 = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("d['st']['nd']['rd']['th'] MMMM")
                        .parseDefaulting(ChronoField.YEAR, currentYear)
                        .toFormatter();
                    return LocalDate.parse(dateString, fmt3);
                } catch (DateTimeParseException e3) {
                    // 4. Try MMMM (e.g. September)
                    try {
                        DateTimeFormatter fmt4 = new DateTimeFormatterBuilder()
                            .parseCaseInsensitive()
                            .appendPattern("MMMM")
                            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                            .parseDefaulting(ChronoField.YEAR, currentYear)
                            .toFormatter();
                        return LocalDate.parse(dateString, fmt4);
                    } catch (DateTimeParseException e4) {
                        // If parsing fails, return a far-future date so it appears last
                        return LocalDate.of(2025, 12, 31);
                    }
                }
            }
        }
    }

    public String getTitle()          { return title; }
    public String getClubName()       { return clubName; }
    public String getDateString()     { return dateString; }
    public LocalDate getDate()        { return date; }
    public String getDepartment()     { return department; }
    public String getDescription()    { return description; }
    public String getLink()           { return link; } // <-- Add this getter

    // Returns true if the event is today or in the future
    public boolean isUpcoming() {
        return !date.isBefore(LocalDate.now());
    }

    // If you want to allow updating the date from a string (e.g. from a TextField)
    public void setDateFromString(String newDateString) {
        this.dateString = newDateString;
        this.date = parseDate(newDateString);
    }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
