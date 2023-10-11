//変数の定義

var websocket = null;
var shakeList = ["","shake-hard","shake-slow","shake-little","shake-horizontal","shake-vertical","shake-rotate","shake-opacity","shake-crazy"];
var shakeChinese = ["","力が揺れ動く","雪がちらつく","ぶるぶる震える","左右に揺れる","上下に鼓動する","シーソー本","ゆらゆら揺れる","治療を放棄する"];
var aa = '<div class="botui-message-left"><div class="botui-message-content-img" onclick="originalImage(this)">';
var b = '</div></div>';
var cc = '<div class="botui-message-right"><div class="botui-message-content2-img" onclick="originalImage(this)">';
var host = location.host;
var wsHost = "ws://"+host+"/websocket";
var focus = false;
var mute = 2;
var shieldMap = new Map();
var timer;
var shakeNum = 0;
var msgSwitchTips = 'メッセージ通知のオン/オフを切り替えるにはクリックしてください';
var emojiTips = '万衆期待(誰にも聞かれない)の表情パック機能がついに来た';
var pictureTips = 'クリックして画像を送信（最大1 M対応の画像）';
var shakeTips = '手ぶれメッセージを送って注目を集めてみましょう。全部で7種類の手ぶれ効果がありますよ（「Esc」キーはこの機能をすばやくオフにして、手ぶれメッセージをダブルクリックすると彼を止めることができます)';
var clearTips = 'クリーンスクリーン、チャットの記録は保存されませんよ！！！';
var sendTips = 'クリックしてメッセージを送信（車に戻ってもメッセージを送信できます）';
var onerrorMsg = "サーバーへの接続でエラーが発生しました。ページを更新して再入力してください！";
var oncloseMsg = 'サーバーから切断されました！';
var unSupportWsMsg = "現在のブラウザはWebSocketをサポートしていません";
var firstTips = "<b>この粗末なチャットルームを試してみて、いくつかの隠し機能を話してくれてありがとうございます。</b><br>1.サイドバーにメンバーのリストが表示され、メンバーの左にある小さな円形をクリックすることで、送信されたメッセージが受信されないようにすることができますが、メッセージを受け取ることができます<br>2.各ボタンにマウスポインタを置くと使用説明がポップアップされます<br>3.ブラウザがフロントにいない場合は、提示音とデスクトップ通知があり、嫌なら左上のスピーカーをクリックして閉じることができます";
var emojiPath = 'dist/img/';
var emojiHead = '<img class="emoji_icon" src="'+emojiPath;
var textHead = '⇤';
var emojiFoot = '">';
var textFoot = '⇥';

