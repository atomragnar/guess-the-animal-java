package animals.tree;


/*

    Right child == YES
    Left child == NO

 */

import animals.languagerules.AnimalNLPService;
import static animals.languagerules.AnimalNLPService.*;

import java.util.*;
import java.util.function.UnaryOperator;

import static animals.languagerules.AnimalNLPService.StringFormatter.TREE_STATS;

public class AnimalFactTree {

    // helper method for printing the distinguishing fact
    // helper method for printing the question in the node

    Node root;
    Node current;

    public AnimalFactTree() {
        this.root = null;
        this.current = null;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(String data) {
        this.root = new Node(data, NodeLabel.ROOT);
        this.current = root;
    }

    public void setRoot(Node root) {
        this.root = root;
        this.root.setLabel(this.root.getLabel() == null
                ? NodeLabel.ROOT
                : this.root.getLabel()
        );
        this.current = root;
    }

    public Node getCurrent() {
        return current;
    }

    public void setCurrent(Node current) {
        this.current = current;
    }

    public boolean isAnimal() {
        return current.isLeaf();
    }

    public boolean isFact() {
        return !current.isLeaf();
    }

    public void insertFact(String fact, String animal, boolean isTrue) {
        String currentData = this.current.getData();
        this.current.setData(fact);
        this.current.setRightChild(isTrue ? new Node(animal) : new Node(currentData));
        this.current.setLeftChild(isTrue ? new Node(currentData) : new Node(animal));
    }

    public void insertAnimal(String animal, boolean isTrue) {
        if (isTrue) {
            this.current.setRightChild(new Node(animal));
        } else {
            this.current.setLeftChild(new Node(animal));
        }
    }

    public String getCurrData() {
        return this.current.getData();
    }

    public String getLeftData() {
        return this.current.getLeftChild().getData();
    }

    public String getRightData() {
        return this.current.getRightChild().getData();
    }

    public void next(boolean isRight) {
        this.current = isRight ? current.getRightChild() : current.getLeftChild();
    }

    public void insert(String data) {
        this.current.setData(data);
    }

    public void delete(String data) {

    }

    public List<String> getAnimals() {
        return getAnimalsAndNodes().stream()
                .map(Map.Entry::getKey)
                .sorted().toList();
    }

    private List<Map.Entry<String, Node>> getAnimalsAndNodes() {
        return findAllAnimals(new ArrayList<>(), root);
    }

    private List<Map.Entry<String, Node>> findAllAnimals(List<Map.Entry<String, Node>> animals, Node node) {
        if (node.isLeaf()) {
            animals.add(new AbstractMap.SimpleEntry<>(node.getData(), node));
        } else {
            findAllAnimals(animals, node.getLeftChild());
            findAllAnimals(animals, node.getRightChild());
        }
        return animals;
    }

    private List<String> getFacts() {
        return findAllFacts(new ArrayList<>(), root);
    }

    private List<String> findAllFacts(List<String> facts, Node node) {
        if (!node.isLeaf()) {
            facts.add(node.getData());
            findAllFacts(facts, node.getLeftChild());
            findAllFacts(facts, node.getRightChild());
        }
        return facts;
    }

    public List<Integer> getDepths() {
        return findAllDepths(new ArrayList<>(), root, 0);
    }

    private List<Integer> findAllDepths(List<Integer> depths, Node node, int n) {
        if (node.isLeaf()) depths.add(n);
        else {
            findAllDepths(depths, node.getLeftChild(), n + 1);
            findAllDepths(depths, node.getRightChild(), n + 1);
        }
        return depths;
    }

    public String getAnimalTreeStats(String prompt) {
        String rootData = AnimalNLPService.StringFormatter.capitalizeFirstLetter.apply(root.getData());
        List<Map.Entry<String, Node>> animals = getAnimalsAndNodes();
        List<String> facts = getFacts();
        List<Integer> depths = getDepths();


        int[] stats = new int[5];
        stats[0] = animals.size() + facts.size();
        stats[1] = animals.size();
        stats[2] = facts.size();
        stats[3] = depths.stream().mapToInt(i -> i).max().orElse(0);
        stats[4] = depths.stream().mapToInt(i -> i).min().orElse(0);

        Double average = depths.stream().mapToDouble(i -> i).average().orElse(0);

        return TREE_STATS.apply(rootData, stats, average, prompt);

    }

    public Optional<List<Map.Entry<String, Boolean>>> findAnimal(String animal) {
        return Optional.ofNullable(findAnimalAndFacts(animal, root, new ArrayList<>()));
    }

    private List<Map.Entry<String, Boolean>> findAnimalAndFacts(String animal, Node node, List<Map.Entry<String, Boolean>> facts) {
        if (node.isLeaf()) {
            String data = StringFormatter.REPLACE_PRONOUN.apply(node.getData());
            if (data.equals(animal)) {
                return facts;
            } else {
                return null;
            }
        }
        List<Map.Entry<String, Boolean>> left = new ArrayList<>(facts);
        left.add(new AbstractMap.SimpleEntry<>(node.getData(), false));
        List<Map.Entry<String, Boolean>> right = new ArrayList<>(facts);
        right.add(new AbstractMap.SimpleEntry<>(node.getData(), true));

        List<Map.Entry<String, Boolean>> foundPath = findAnimalAndFacts(animal, node.getLeftChild(), left);
        if (foundPath == null) {
            foundPath = findAnimalAndFacts(animal, node.getRightChild(), right);
        }
        return foundPath;
    }

    public String toString() {
        UnaryOperator<String> format = s -> StringFormatter.QUESTION.apply(s);
        StringBuilder sb = new StringBuilder();
        printNodes(root, sb, "└ ", "  ", format);
        return sb.toString();
    }

    private void printNodes(Node node, StringBuilder sb, String prefix, String childPrefix, UnaryOperator<String> format) {
        sb.append(prefix);
        sb.append(!node.isLeaf()
                ? format.apply(node.getData())
                : node.getData()
        );
        sb.append("\n");

        if (!node.isLeaf()) {
            printNodes(node.getLeftChild(), sb, childPrefix + "├ ", childPrefix + "│ ", format);
            printNodes(node.getRightChild(), sb, childPrefix + "└ ", childPrefix + "  ", format);
        }
    }

    public boolean isFresh() {
        return getCurrData() == null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void reset() {
        this.current = root;
    }
}
