# 開発ログ

## Day1

### 作業内容

- アプリ名を決定
- アプリコンセプトを整理
- MVP要件を整理
- 画面構成を整理
- README.mdを作成
- requirements.mdを作成
- SDK 37へのアップデートとビルドエラーの解消

### 決定事項

- アプリ名は「ピンイン耳トレ - HSK1級対応」
- 最初はHSK1級の約150語を対象にする
- 音声はAndroid TextToSpeechを使う
- 出題形式はピンイン＋声調の3択 / 5択
- ランキングはSharedPreferencesで保存する

### 次回作業

Day2ではホーム画面を作成する。 (完了)

## Day2

### 作業内容

- ホーム画面のレイアウト作成 (XML)
- タイトル、最高スコア、ランキング（仮データ）を表示
- 問題数と選択肢数の選択用 Spinner を配置
- STARTボタンの実装（Toast表示まで）

### 決定事項

- Spinner の選択肢は 10/20/50問、3択/5択とする

### 次回作業

Day3ではクイズ画面の実装を行う。(完了)

- クイズ画面のレイアウト作成
- ホーム画面からの遷移と設定値の引き継ぎ

## Day3

### 作業内容

- QuizActivity と activity_quiz.xml の作成
- MainActivity から QuizActivity への遷移実装
- Intent による問題数・選択肢数のデータ受け渡し
- 選択肢数（3択/5択）に応じたボタンの動的表示制御
- クイズ画面のモックUI（再生ボタン、進捗表示、選択肢ボタン）作成

### 決定事項

- 選択肢ボタンはタップ時にどのピンインを選んだかToastで出す（デバッグ用）

### 次回作業

Day4ではクイズのコアロジックを実装する。(一部完了)

- 回答結果画面の作成
- 正誤判定と画面遷移の実装

## Day4

### 作業内容

- AnswerActivity と activity_answer.xml の作成
- QuizActivity から AnswerActivity への遷移実装
- 正誤判定ロジック（モックデータ使用）の作成
- 回答結果画面のUI作成（正解・不正解の表示、単語情報表示）
- 「次へ」ボタンによる QuizActivity への復帰実装

### 決定事項

- 正解時は「正解！」、不正解時は「不正解」と正解のピンインを表示する
- 回答結果画面には対象単語の漢字、ピンイン、日本語意味を表示する

### 次回作業

Day5ではクイズのコアロジックを強化する。(完了)

- 1プレイ完走機能の実装（Quiz -> Answer -> Result）
- 状態管理（問題番号、正解数）の実装

## Day5

### 作業内容

- ResultActivity と activity_result.xml の作成
- QuizActivity, AnswerActivity 間の状態（問題数、現在の問題、正解数）の受け渡し実装
- クイズのループ処理（次へボタンで次の問題、または結果画面へ）の実装
- 結果画面でのスコア・正答率計算ロジックの実装
- ホームへ戻るボタンの実装（バックスタック管理）

### 決定事項

- 1問正解につき100点とする
- 結果画面からはMainActivityに `FLAG_ACTIVITY_CLEAR_TOP` で戻る

### 次回作業

Day6では本物のデータと音声を追加する。(一部完了)

- 単語データモデルの定義
- 仮単語リスト（10語）による出題の実装

## Day6

### 作業内容

- `Word` データクラスと `WordData` オブジェクトの作成（`kotlin-parcelize` を使用）
- `QuizActivity` において、`currentQuestion` に基づいて `WordData` から単語を選択するロジックを実装
- `AnswerActivity` への `Word` オブジェクトの受け渡しと表示の実装
- `build.gradle.kts` への `kotlin-parcelize` プラグインの追加

### 決定事項

- 単語データの受け渡しには `Parcelable` を使用する
- 現時点では `currentQuestion` 順に出題し、リスト末尾に達したらループさせる

### 次回作業

Day7では単語データをCSV化し、外部ファイルから読み込む。(完了)

- CSVファイルの作成とassetsへの配置
- Wordモデルの更新とリポジトリクラスの作成

## Day7

### 作業内容

- `app/src/main/assets/hsk1_words.csv` の作成（10語分）
- `Word` データクラスに `level` フィールドを追加
- `WordRepository` クラスを実装し、assets内のCSVからデータを読み込む機能を実装
- `QuizActivity` および `AnswerActivity` を更新し、CSVから読み込んだデータを使用するように変更

### 決定事項

- 単語データはCSV形式で管理し、`assets` フォルダに配置する
- 読み込み失敗時のために、仮データのフォールバックを実装する

### 次回作業

Day8ではTextToSpeechを実装し、実際の音声を再生する。

- TextToSpeechの初期化
- 再生ボタン押下時の音声出力
- クイズ開始時の自動再生
