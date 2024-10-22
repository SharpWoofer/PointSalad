# 🥗 Point Salad - Online Card Drafting Game

![image_proxy](https://github.com/user-attachments/assets/6fe871ad-8bd2-4fac-9402-c89d5731f484)

Point Salad is an online card-drafting game where players take turns building a salad of veggies and collecting point cards. This game is inspired by the board game Point Salad and supports 2-6 players. In future expansions, this project may support variations such as Point City. The game is designed with extensibility and modularity in mind, so additional features can be added easily!

## 🚀 Features

- **2-6 players:** Engage with friends or bots, with varying deck sizes based on the number of players.
- **Online multiplayer:** Play online by connecting clients to the server.
- **Card-drafting mechanics:** Draft point or vegetable cards to score points and win the game.
- **Dynamic scoring:** Flip point cards to vegetables to maximize your score.

## 🎮 How to Play

1. Clone the repository.

2. Compile the game:

    ```bash
    javac -cp .:json.jar PointSalad.java
    ```

3. Start the server:

    ```bash
    java -cp .:json.jar PointSalad
    ```

4. **Optional:** Start the server with arguments:

    ```bash
    java -cp .:json.jar PointSalad [#Players] [#Bots]
    ```

5. Connect an online client:

    ```bash
    java -cp .:json.jar PointSalad 127.0.0.1
    ```

## 🧪 Running Unit Tests

This project includes comprehensive unit tests to ensure the rules of the game are properly enforced. To run the tests:

1. Make sure you have JUnit 5 set up.
2. In your terminal, run the following command:

    ```bash
    mvn test
    ```

### Key Test Cases

- ✅ Validating the number of players (2-6).
- ✅ Ensuring deck sizes adjust based on the number of players.
- ✅ Checking market setup, card drafting, and score calculations.
- ✅ Ensuring the correct winner is declared.

## 📜 Rules Overview

Point Salad is played with 108 cards that have two sides: a vegetable side and a point card side. Players take turns drafting cards and trying to score points based on the criteria specified on the point cards. Here’s a short overview of the rules:

- **Player Setup:** Choose 2-6 players.
- **Deck:** Cards consist of vegetables (Tomato, Lettuce, etc.) and point criteria. The number of cards in play depends on the number of players.
- **Game Flow:** On your turn, you may either:
  - Draft one point card from the draw pile.
  - Draft two vegetable cards from the market.
- **Scoring:** Based on the point cards in your hand, calculate your score using your veggies.
- **Winner:** The player with the most points at the end of the game wins!
  
For detailed rules, check out the [Point Salad Rulebook](assets/PointSalad_Rulebook.pdf).

## 🔧 Tech Stack

- **Java 17** for game logic and application.
- **JUnit 5** for unit testing.
- **Maven** for project management.
- **JSON** to load the game’s card manifest.

## 📚 Future Expansion

This game is designed with extensibility in mind to easily support new variations like [Point City](assets/PointCity_RuleBook_web.pdf). Future expansions could introduce:

- Additional card mechanics
- New types of player interactions
- Enhanced AI for bot players

## 📄 License
This project is licensed under the MIT License. See the [LICENSE file](LICENSE) for details.
