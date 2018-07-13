package pos.unipe.com.br.jogodavelha;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private View view;

    private Button btnPlay;

    private final String BOLA = "O";
    private final String XIS = "X";

    private String firstPlay = "X";
    private String lastPlay = "X";
    private int gameState = 0;

    int[][] estadoFinal = new int[][]{

            //Verificar linhas
        {1,2,3},
        {4,5,6},
        {7,8,9},

            //Verificar colunas
        {1,4,7},
        {2,5,8},
        {3,6,9},

            //Verificar diagonal
        {1,5,9},
        {3,5,7}
    };

    private int totalPlays = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configura a view

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        setView(inflater.inflate(R.layout.activity_main, null));

        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.idPlay);


        //Atribui ao botão, a função de iniciar o jogo
        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newGame(v);
            }
        });

        //Atribui aos botões do jogo, a função de efetuar a jogada
        for(int i = 1; i <= 9; i++){
            Button b = getSelected(i);
            if(b != null){
                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        clickSelected(v);
                    }
                });
            }
        }
    }

    public void newGame(View view){
        //Ajusta as cores dos textos dos botões para preto
        setBlacks();
        //Limpa os textos dos botões
        cleanBtns();
        //Habilita o clique nos botões
        toogleBtns();
        //Ajusta o contador de jogadas
        setTotalPlays(0);

        RadioButton rx = findViewById(R.id.rdX);
        RadioButton ro = findViewById(R.id.rdO);
        RadioGroup rdg = findViewById(R.id.rdg);

        btnPlay.setText("Encerrar");

        //Verifica qual é a escolha de primeira jogada.
        if(rdg.getCheckedRadioButtonId() != -1) {
            RadioButton rdSelected = (RadioButton) findViewById(rdg.getCheckedRadioButtonId());

            setFirstPlay(rdSelected.getText().toString());

            rx.setVisibility(View.INVISIBLE);
            ro.setVisibility(View.INVISIBLE);

            setFirstPlay(rdSelected.getText().toString());

            TextView resultText = findViewById(R.id.idResult);
            resultText.setText("Jogo iniciado por: "+getFirstPlay());

        }
        setGameState(1);

        //Troca a função do botão
        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                terminateGame();
            }
        });

    }

    public void terminateGame(){
        TextView resultText = findViewById(R.id.idResult);
        resultText.setText("Jogo Finalizado");
        endGame();
    }

    public void endGame(){
        toogleBtns();

        Toast.makeText(getView().getContext(), "Fim de jogo", Toast.LENGTH_LONG).show();
        btnPlay.setText("Novo Jogo");

        RadioButton rx = findViewById(R.id.rdX);
        RadioButton ro = findViewById(R.id.rdO);

        rx.setVisibility(View.VISIBLE);
        ro.setVisibility(View.VISIBLE);

        rx.setSelected(false);
        ro.setSelected(false);

        setGameState(0);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newGame(v);
            }
        });
    }

    public void setBlacks(){
        for(int i = 1; i <= 9; i++){
            Button b = getSelected(i);
            if(b != null){
                b.setTextColor(getResources().getColor(R.color.black));

            }
        }
    }
    public void toogleBtns(){
        for(int i = 1; i <= 9; i++){
            Button b = getSelected(i);
            if(b != null){
                b.setEnabled(!b.isEnabled());

            }
        }
    }

    public void cleanBtns(){
        for(int i = 1; i <= 9; i++){
            Button b = getSelected(i);
            if(b != null){
                b.setText("");
            }
        }
    }



    public Button getSelected(int tagNum){
        Button b = (Button) getView().findViewWithTag(String.valueOf(tagNum));

        return (Button) findViewById(b.getId());
    }

    public void clickSelected(View view) {
        Button b = (Button) view;
        String bText = b.getText().toString();
        //Verifica se o botão já foi clicado
        if (bText.equals("")){

            //Atualiza o contador de jogadas
            addTotalPlays();
            //Verifica o jogador da vez
            if(getFirstPlay() != ""){
                b.setText(getFirstPlay());
                setLastPlay(getFirstPlay());
                setFirstPlay("");
            }else if (getLastPlay().equals(XIS)) {
                b.setText(BOLA);
                setLastPlay(BOLA);
            } else {
                b.setText(XIS);
                setLastPlay(XIS);
            }

            //Verifica se o jogo terminou
            if(isEndGame()){
                //Finaliza jogo
                endGame();
            }
        }else{
            Toast.makeText(getView().getContext(), "Jogada inválida", Toast.LENGTH_SHORT).show();
        }


    }

    public void setColor(int btn, int color){
        getSelected(btn).setTextColor(getResources().getColor(color));
    }

    public boolean isEndGame(){
        // Percorre o array de possibilidades de fim de jgoo
        for (int x = 0; x <= 7; x++){
            String s1 = getSelected(estadoFinal[x][0]).getText().toString();
            String s2 = getSelected(estadoFinal[x][1]).getText().toString();
            String s3 = getSelected(estadoFinal[x][2]).getText().toString();

            //Verifica se as posições estão preenchidas
            if(!s1.equals("") && !s2.equals("") && !s3.equals("")){
                //Verifica se o jogo foi ganho
                if(s1.equals(s2) && s2.equals(s3)){

                    //Muda a cor dos botões vencedores para vermelho
                    setColor(estadoFinal[x][0], R.color.red);
                    setColor(estadoFinal[x][1], R.color.red);
                    setColor(estadoFinal[x][2], R.color.red);

                    //Informa o vencedor
                    TextView resultText = findViewById(R.id.idResult);
                    resultText.setText("Vitória de "+s1);


                    //Contabiliza a vitória no placar do vencedor correspondente
                    if(s1.equals(XIS)){
                        //X VENCEU
                        TextView tv = findViewById(R.id.idVX);
                        int vitorias = Integer.parseInt(tv.getText().toString());
                        tv.setText(String.valueOf(++vitorias));
                    }else{
                        //O VENCEU
                        TextView tv = findViewById(R.id.idVO);
                        int vitorias = Integer.parseInt(tv.getText().toString());
                        tv.setText(String.valueOf(++vitorias));
                    }

                    return true;
                }
            }


        }
        //Verifica se houve um empate
        if(getTotalPlays() == 9){
            TextView resultText = findViewById(R.id.idResult);
            resultText.setText("Empate Técnico");

            return true;
        }
        return false;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getLastPlay() {
        return lastPlay;
    }

    public void setLastPlay(String lastPlay) {
        this.lastPlay = lastPlay;
    }

    public String getFirstPlay() {
        return firstPlay;
    }

    public void setFirstPlay(String firstPlay) {
        this.firstPlay = firstPlay;
    }

    public int getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(int totalPlays) {
        this.totalPlays = totalPlays;
    }

    public void addTotalPlays() {
        int i = getTotalPlays();
        this.totalPlays = ++i;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }
}
