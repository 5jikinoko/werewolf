package werewolf.store.gamesettings;

public class RoleBreakdown {
    public int	villagersNum;
    public int	seersNum;
    public int	necromancersNum;
    public int	knightsNum;
    public int	huntersNum;
    public int	blackKnightsNum;
    public int	freemasonariesNum;
    public int	bakersNum;
    public int	werewolvesNum;
    public int	madmenNum;
    public int	traitorsMum;
    public int	foxSpiritsNum;
    public int	foolsNum;
    public int	phantomThievesNum;

    /**
     *
     * @param villagersNum 村人の数
     * @param seersNum
     * @param necromancersNum
     * @param knightsNum
     * @param huntersNum
     * @param blackKnightsNum
     * @param freemasonariesNum
     * @param bakersNum
     * @param werewolvesNum
     * @param madmenNum
     * @param traitorsMum
     * @param foxSpiritsNum
     * @param foolsNum
     * @param phantomThievesNum
     */
    public RoleBreakdown (
            int villagersNum,
            int seersNum,
            int necromancersNum,
            int knightsNum,
            int huntersNum,
            int blackKnightsNum,
            int freemasonariesNum,
            int bakersNum,
            int werewolvesNum,
            int madmenNum,
            int traitorsMum,
            int foxSpiritsNum,
            int foolsNum,
            int phantomThievesNum
        ) {
        this.villagersNum = villagersNum;
        this.seersNum = seersNum;
        this.necromancersNum = necromancersNum;
        this.knightsNum = knightsNum;
        this.huntersNum = huntersNum;
        this.blackKnightsNum = blackKnightsNum;
        this.freemasonariesNum = freemasonariesNum;
        this.bakersNum = bakersNum;
        this.werewolvesNum = werewolvesNum;
        this.madmenNum = madmenNum;
        this.traitorsMum = traitorsMum;
        this.foxSpiritsNum = foxSpiritsNum;
        this.foolsNum = foolsNum;
        this.phantomThievesNum = phantomThievesNum;
    }

        public RoleBreakdown () {
        
    }
}
