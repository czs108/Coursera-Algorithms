/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      1/1/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.TreeMap;

public class BaseballElimination {

    /*** Field Index ***/
    private static final int IDX_NAME = 0;
    private static final int IDX_WINS = 1;
    private static final int IDX_LOSS = 2;
    private static final int IDX_LEFT = 3;
    private static final int IDX_LEFT_DETAIL = 4;

    private static final class Team {

        public final int id;
        public final int wins;
        public final int loss;
        public final int left;

        public Team(int id, int wins, int loss, int left) {
            assert (id >= 0);
            assert (wins >= 0 && loss >= 0 && left >= 0);

            this.id = id;
            this.wins = wins;
            this.loss = loss;
            this.left = left;
        }
    }

    private static final class FlowNetworkEx extends FlowNetwork {

        private int capacityFromS;

        public FlowNetworkEx(int V) {
            super(V);
        }

        public void setCapacityFromS(int capacity) {
            assert (capacity > 0);

            capacityFromS = capacity;
        }

        public int getCapacityFromS() {
            return capacityFromS;
        }
    }

    private final ArrayList<String> teamNames = new ArrayList<String>();

    private final TreeMap<String, Team> teamRecords = new TreeMap<String, Team>();

    private final int[][] leftGames;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        In in = new In(filename);
        int id = 0;
        int teamCount = Integer.parseInt(in.readLine());
        leftGames = new int[teamCount][teamCount];
        while (in.hasNextLine()) {
            // it's necessary to trim the string
            String[] line = in.readLine().trim().split(" +");
            String name = line[IDX_NAME];

            int wins = Integer.parseInt(line[IDX_WINS]);
            int loss = Integer.parseInt(line[IDX_LOSS]);
            int left = Integer.parseInt(line[IDX_LEFT]);

            for (int i = 0; i != teamCount; ++i) {
                leftGames[id][i] = Integer.parseInt(line[IDX_LEFT_DETAIL + i]);
            }

            teamNames.add(name);
            teamRecords.put(name, new Team(id, wins, loss, left));

            ++id;
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teamNames.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teamNames;
    }

    // number of wins for given team
    public int wins(String team) {
        checkTeam(team);

        return teamRecords.get(team).wins;
    }

    // number of losses for given team
    public int losses(String team) {
        checkTeam(team);

        return teamRecords.get(team).loss;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        checkTeam(team);

        return teamRecords.get(team).left;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        checkTeam(team1);
        checkTeam(team2);

        int id1 = teamRecords.get(team1).id;
        int id2 = teamRecords.get(team2).id;
        return leftGames[id1][id2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        checkTeam(team);

        FlowNetworkEx network = buildFlowNetwork(team);
        if (network != null) {
            // given team is not eliminated iff all edges pointing from s are full in maxflow
            FordFulkerson maxflow = new FordFulkerson(network, 0, network.V() - 1);
            return network.getCapacityFromS() > maxflow.value();
        } else {
            return true;
        }
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);

        if (!isEliminated(team)) {
            return null;
        }

        ArrayList<String> teams = new ArrayList<String>();
        Team givenTeam = teamRecords.get(team);
        FlowNetworkEx network = buildFlowNetwork(team);
        if (network != null) {
            int teamVertexCount = numberOfTeams() - 1;
            int gameVertexCount = teamVertexCount * (teamVertexCount - 1) / 2;
            int iTeamVertex = gameVertexCount + 1;

            FordFulkerson maxflow = new FordFulkerson(network, 0, network.V() - 1);
            for (int i = 0; i != numberOfTeams(); ++i) {
                if (i == givenTeam.id) {
                    continue;
                }

                // true if there is a path from s to team i in residual network
                // means that this edge is not full in maxflow
                if (maxflow.inCut(iTeamVertex)) {
                    teams.add(teamNames.get(i));
                }

                ++iTeamVertex;
            }

        } else {
            // find the teams which have won more games than the maximum number of games given team can win
            int mostWins = givenTeam.wins + givenTeam.left;
            for (int i = 0; i != numberOfTeams(); ++i) {
                if (i == givenTeam.id) {
                    continue;
                }

                Team currTeam = teamRecords.get(teamNames.get(i));
                if (mostWins < currTeam.wins) {
                    teams.add(teamNames.get(i));
                }
            }
        }

        return teams;
    }

    private FlowNetworkEx buildFlowNetwork(String team) {
        assert (team != null && isTeamValid(team));

        int teamVertexCount = numberOfTeams() - 1;
        int gameVertexCount = teamVertexCount * (teamVertexCount - 1) / 2;
        int vertexCount = gameVertexCount + teamVertexCount + 2;

        Team givenTeam = teamRecords.get(team);
        int mostWins = givenTeam.wins + givenTeam.left;

        // the initial linear index of 4 different parts: s, games, teams and t
        int s = 0;
        int t = vertexCount - 1;
        int gameVertex = 1;
        int iTeamVertex = gameVertexCount + 1;

        // use FlowNetworkEx class, not FlowNetwork class in algs4
        int capacityFromS = 0;
        FlowNetworkEx network = new FlowNetworkEx(vertexCount);
        for (int i = 0; i != numberOfTeams(); ++i) {
            if (i == givenTeam.id) {
                continue;
            }

            // team i has won more games than the maximum number of games given team can win
            // given team is eliminated
            Team currTeam = teamRecords.get(teamNames.get(i));
            if (mostWins < currTeam.wins) {
                return null;
            }

            int jTeamVertex = iTeamVertex + 1;
            for (int j = i + 1; j != numberOfTeams(); ++j) {
                if (j == givenTeam.id) {
                    continue;
                }

                // connect s to the remaining game
                network.addEdge(new FlowEdge(s, gameVertex, leftGames[i][j]));
                capacityFromS += leftGames[i][j];
                // connect the remaining game to team i
                network.addEdge(new FlowEdge(gameVertex, iTeamVertex, Double.POSITIVE_INFINITY));
                // connect the remaining game to team j
                network.addEdge(new FlowEdge(gameVertex, jTeamVertex, Double.POSITIVE_INFINITY));

                ++gameVertex;
                ++jTeamVertex;
            }

            // connect team i to t
            network.addEdge(new FlowEdge(iTeamVertex, t, mostWins - currTeam.wins));

            ++iTeamVertex;
        }

        network.setCapacityFromS(capacityFromS);
        return network;
    }

    private void checkTeam(String team) {
        if (team == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        if (!isTeamValid(team)) {
            throw new IllegalArgumentException("[!] The team is invalid");
        }
    }

    private boolean isTeamValid(String team) {
        assert (team != null);

        return teamRecords.containsKey(team);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
