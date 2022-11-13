package lotto.model.domain;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lotto.utils.Utils;

public class WinningNumber {

    private static final String NUMBER_SEPARATOR = ",";
    private static final String WINNING_NUMBER_REGEX = "^[0-9]*,[0-9]*,[0-9]*,[0-9]*,[0-9]*,[0-9]*$";
    private static final String BONUS_NUMBER_REGEX = "^[0-9]*";
    private static final String WRONG_WINNING_NUMBER_FORM = "올바르지 않은 당첨 번호 형식 입니다.";
    private static final String BONUS_NUMBER_IS_NOT_NUMBER = "보너스 번호는 숫자 이어야 합니다.";
    private static final String INVALID_BONUS_NUMBER_RANGE = "보너스 번호는 1이상 45 이어야 합니다.";
    private static final String DUPLICATED_BONUS_NUMBER = "보너스 번호는 당첨 번호와 중복되지 않아야 합니다.";
    private static final int LOTTO_NUM_MIN = 1;
    private static final int LOTTO_NUM_MAX = 45;
    private static final double THIRD_SCORE = 5;
    private static final double SECOND_SCORE = 5.5;

    private final Lotto lotto;
    private final int bonusNumber;

    public WinningNumber(String winningNumber, String bonusNumber) {
        winningNumber = validateWinningNumber(winningNumber);
        List<Integer> parsedWinningNumber = parseWinningNumber(winningNumber);

        lotto = new Lotto(parsedWinningNumber);
        this.bonusNumber = validateBonusNumber(bonusNumber, parsedWinningNumber);
    }

    private int validateBonusNumber(String bonusNumber, List<Integer> winningNumber) {
        bonusNumber = Utils.deleteAllString(bonusNumber);
        if (!Pattern.matches(BONUS_NUMBER_REGEX, bonusNumber)) {
            throw new IllegalArgumentException(BONUS_NUMBER_IS_NOT_NUMBER);
        }
        return validateBonusNumber(Integer.parseInt(bonusNumber), winningNumber);
    }

    private int validateBonusNumber(int bonusNumber, List<Integer> winningNumber) {
        if (isInvalidLottoNumberRange(bonusNumber)) {
            throw new IllegalArgumentException(INVALID_BONUS_NUMBER_RANGE);
        }
        if (isDuplicatedWithWinningNumber(bonusNumber, winningNumber)) {
            throw new IllegalArgumentException(DUPLICATED_BONUS_NUMBER);
        }
        return bonusNumber;
    }

    private boolean isDuplicatedWithWinningNumber(int bonusNumber, List<Integer> winningNumber) {
        return winningNumber.contains(bonusNumber);
    }

    private String validateWinningNumber(String winningNumber) {
        winningNumber = Utils.deleteAllString(winningNumber);
        if (!Pattern.matches(WINNING_NUMBER_REGEX, winningNumber)) {
            throw new IllegalArgumentException(WRONG_WINNING_NUMBER_FORM);
        }
        return winningNumber;
    }

    private boolean isInvalidLottoNumberRange(int num) {
        return num < LOTTO_NUM_MIN || num > LOTTO_NUM_MAX;
    }

    private List<Integer> parseWinningNumber(String winningNumber) {
        return Arrays.stream(winningNumber.split(NUMBER_SEPARATOR))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public List<Rank> collectRanks(List<Lotto> lottos) {
        return lottos.stream()
                .map(this::createRank)
                .collect(Collectors.toList());
    }

    private Rank createRank(Lotto lotto) {
        double numOfMatch = this.lotto.countMatch(lotto);
        boolean isBonusMatch = lotto.contains(bonusNumber);

        if (isSecondScore(numOfMatch, isBonusMatch)) {
            numOfMatch = SECOND_SCORE;
        }
        return RankCreator.create(numOfMatch);
    }

    private boolean isSecondScore(double numOfMatch, boolean isBonusMatch) {
        return numOfMatch == THIRD_SCORE && isBonusMatch;
    }

}
