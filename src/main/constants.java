import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GameData {

    private static final String BUILDING_FILE = "building.json";
    private static final String RESOURCES_FILE = "resources.json";
    private static final String COMPONENTS_FILE = "components.json";
    private static final String FOODS_FILE = "foods.json";
    private static final String CROPS_FILE = "crops.json";

    private Map<String, Building> buildings = new HashMap<>();
    private Map<String, Resource> resources = new HashMap<>();
    private Map<String, Component> components = new HashMap<>();
    private Map<String, Food> foods = new HashMap<>();
    private Map<String, Crop> crops = new HashMap<>();

    private static GameData instance;

    private GameData() {
        loadBuildings();
        loadResources();
        loadComponents();
        loadFoods();
        loadCrops();
    }

    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    private void loadBuildings() {
        loadBuildingData(BUILDING_FILE, buildings, Building.class);
    }

    private void loadResources() {
        loadResourceData(RESOURCES_FILE, resources, Resource.class);
    }

    private void loadComponents() {
        loadResourceData(COMPONENTS_FILE, components, Component.class);
    }

    private void loadFoods() {
        loadResourceData(FOODS_FILE, foods, Food.class);
    }

    private void loadCrops() {
        loadResourceData(CROPS_FILE, crops, Crop.class);
    }

    private <T extends Resource> void loadResourceData(String filename, Map<String, T> resourceMap, Class<T> resourceClass) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String description = jsonObject.getString("description");
                String type = jsonObject.getString("type");
                int value = jsonObject.getInt("value");
                int stackSize = jsonObject.getInt("stackSize");

                T resource;

                if (resourceClass == Component.class) {
                    JSONArray requirementsJson = jsonObject.getJSONArray("requirements");
                    Map<String, Integer> requirements = new HashMap<>();
                    for (int j = 0; j < requirementsJson.length(); j++) {
                        JSONObject requirementJson = requirementsJson.getJSONObject(j);
                        String requirementId = requirementJson.getString("id");
                        int amount = requirementJson.getInt("amount");
                        requirements.put(requirementId, amount);
                    }
                    resource = (T) new Component(id, name, description, type, value, stackSize, requirements);
                } else if (resourceClass == Food.class) {
                    int hungerRestore = jsonObject.getInt("hungerRestore");
                    int cookTime = jsonObject.getInt("cookTime");

                    JSONArray requirementsJson = jsonObject.getJSONArray("requirements");
                    Map<String, Integer> requirements = new HashMap<>();
                    for (int j = 0; j < requirementsJson.length(); j++) {
                        JSONObject requirementJson = requirementsJson.getJSONObject(j);
                        String requirementId = requirementJson.getString("id");
                        int amount = requirementJson.getInt("amount");
                        requirements.put(requirementId, amount);
                    }
                    resource = (T) new Food(id, name, description, type, value, stackSize, hungerRestore, cookTime, requirements);
                } else if (resourceClass == Crop.class){
                   int growTime = jsonObject.getInt("growTime");
                   int yield = jsonObject.getInt("yield");
                   resource = (T) new Crop(id,name,description,type,value,stackSize,growTime,yield);
                } else if (resourceClass == Building.class) {
                    int health = jsonObject.getInt("health");
                    int armor = jsonObject.getInt("armor");

                    JSONArray requirementsJson = jsonObject.getJSONArray("requirements");
                    Map<String, Integer> requirements = new HashMap<>();
                    for (int j = 0; j < requirementsJson.length(); j++) {
                        JSONObject requirementJson = requirementsJson.getJSONObject(j);
                        String requirementId = requirementJson.getString("id");
                        int amount = requirementJson.getInt("amount");
                        requirements.put(requirementId, amount);
                    }
                    resource = (T) new Building(id, name, description, type, value, stackSize, health, armor, requirements);
                }
                else {
                    resource = (T) new Resource(id, name, description, type, value, stackSize);
                }

                resourceMap.put(id, resource);
            }

        } catch (IOException e) {
            System.err.println("Error loading resources from " + filename + ": " + e.getMessage());
        }
    }


    public Building getBuilding(String id) {
        return buildings.get(id);
    }

    public Resource getResource(String id) {
        return resources.get(id);
    }

    public Component getComponent(String id) {
        return components.get(id);
    }

    public Food getFood(String id) {
        return foods.get(id);
    }

    public Crop getCrop(String id) { return crops.get(id); }

    // Вспомогательные классы для ресурсов
    public static class Resource {
        private String id;
        private String name;
        private String description;
        private String type;
        private int value;
        private int stackSize;

        public Resource(String id, String name, String description, String type, int value, int stackSize) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.value = value;
            this.stackSize = stackSize;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getType() {
            return type;
        }

        public int getValue() {
            return value;
        }

        public int getStackSize() {
            return stackSize;
        }

        @Override
        public String toString() {
            return "Resource{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", value=" + value +
                    ", stackSize=" + stackSize +
                    '}';
        }
    }

    public static class Component extends Resource {
        private Map<String, Integer> requirements;

        public Component(String id, String name, String description, String type, int value, int stackSize, Map<String, Integer> requirements) {
            super(id, name, description, type, value, stackSize);
            this.requirements = requirements;
        }

        public Map<String, Integer> getRequirements() {
            return requirements;
        }

        @Override
        public String toString() {
            return "Component{" +
                    "id='" + getId() + '\'' +
                    ", name='" + getName() + '\'' +
                    ", description='" + getDescription() + '\'' +
                    ", type='" + getType() + '\'' +
                    ", value=" + getValue() +
                    ", stackSize=" + getStackSize() +
                    ", requirements=" + requirements +
                    '}';
        }
    }

    public static class Food extends Resource {
        private int hungerRestore;
        private int cookTime;
        private Map<String, Integer> requirements;

        public Food(String id, String name, String description, String type, int value, int stackSize, int hungerRestore, int cookTime, Map<String, Integer> requirements) {
            super(id, name, description, type, value, stackSize);
            this.hungerRestore = hungerRestore;
            this.cookTime = cookTime;
            this.requirements = requirements;
        }

        public int getHungerRestore() {
            return hungerRestore;
        }

        public int getCookTime() {
            return cookTime;
        }

        public Map<String, Integer> getRequirements() {
            return requirements;
        }

        @Override
        public String toString() {
            return "Food{" +
                    "id='" + getId() + '\'' +
                    ", name='" + getName() + '\'' +
                    ", description='" + getDescription() + '\'' +
                    ", type='" + getType() + '\'' +
                    ", value=" + getValue() +
                    ", stackSize=" + getStackSize() +
                    ", hungerRestore=" + hungerRestore +
                    ", cookTime=" + cookTime +
                    ", requirements=" + requirements +
                    '}';
        }
    }

    public static class Crop extends Resource {
        private int growTime;
        private int yield;

        public Crop(String id, String name, String description, String type, int value, int stackSize, int growTime, int yield) {
            super(id, name, description, type, value, stackSize);
            this.growTime = growTime;
            this.yield = yield;
        }

        public int getGrowTime() { return growTime; }

        public int getYield() { return yield; }

        @Override
        public String toString() {
            return "Crop{" +
                    "id='" + getId() + '\'' +
                    ", name='" + getName() + '\'' +
                    ", description='" + getDescription() + '\'' +
                    ", type='" + getType() + '\'' +
                    ", value=" + getValue() +
                    ", stackSize=" + getStackSize() +
                    ", growTime=" + growTime +
                    ", yield=" + yield +
                    '}';
        }
    }

    public static class Building extends Resource{
        private int health;
        private int armor;
        private Map<String, Integer> requirements;

        public Building(String id, String name, String description, String type, int value, int stackSize, int health, int armor, Map<String, Integer> requirements) {
            super(id, name, description, type, value, stackSize);
            this.health = health;
            this.armor = armor;
            this.requirements = requirements;
        }

        public int getHealth() {
            return health;
        }

        public int getArmor() {
            return armor;
        }

        public Map<String, Integer> getRequirements() {
            return requirements;
        }

        @Override
        public String toString() {
            return "Building{" +
                    "id='" + getId() + '\'' +
                    ", name='" + getName() + '\'' +
                    ", description='" + getDescription() + '\'' +
                    ", type='" + getType() + '\'' +
                    ", value=" + getValue() +
                    ", stackSize=" + getStackSize() +
                    ", health=" + health +
                    ", armor=" + armor +
                    ", requirements=" + requirements +
                    '}';
        }
    }

    public static void main(String[] args) {
        GameData gameData = GameData.getInstance();

        Building wall = gameData.getBuilding("wall");
        System.out.println(wall);

        Resource iron = gameData.getResource("iron");
        System.out.println(iron);

        Component glass = gameData.getComponent("glass");
        System.out.println(glass);

        Food frenchFries = gameData.getFood("french_fries");
        System.out.println(frenchFries);

        Crop potato = gameData.getCrop("potato");
        System.out.println(potato);
    }
}
