package main.java.com.terrafutura.game;

public enum GameState {
    TakeCardNoCardDiscarded,
    TakeCardCardDiscarded,
    ActivateCard,
    SelectReward,
    SelectActivationPattern,
    SelectScoringMethod,
    Finish;

    public GameState nextState() {
        return switch (this) {
            case TakeCardNoCardDiscarded, TakeCardCardDiscarded -> ActivateCard;
            case ActivateCard -> SelectReward;
            case SelectReward -> SelectActivationPattern;
            case SelectActivationPattern -> SelectScoringMethod;
            case SelectScoringMethod -> Finish;
            case Finish -> null;
        };
    }
}