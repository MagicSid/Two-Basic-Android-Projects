using Android.App;
using Android.OS;
using Android.Support.V7.App;
using Android.Runtime;
using Android.Widget;
using System.Collections.Generic;
using System;
using System.IO;

namespace MobileHandicapApp
{
    [Activity(Label = "@string/app_name", Theme = "@style/AppTheme", MainLauncher = true)]
    public class MainActivity : AppCompatActivity
    {
        private List<string[,]> scores;
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            
            SetContentView(Resource.Layout.activity_main);
            ConfigureSaveRoundButton();
            ConfigureCalculateDifferenceButton();
            scores = new List<string[,]>();
        }

        private void ConfigureSaveRoundButton()
        {
            Button button = FindViewById<Button>(Resource.Id.saveRoundButton);
            button.Click += delegate
            {
                StartActivity(typeof(SaveRound));
            };
        }

        private void ConfigureCalculateDifferenceButton()
        {
            Button button = FindViewById<Button>(Resource.Id.calcParDifference);
            button.Click += delegate
            {
                int filenums = 0;
                while (CheckFileExists("Scores" + filenums))
                {
                    string fullfilename = Path.Combine(System.Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData), "Scores" + filenums + ".txt");
                    var lines = File.ReadAllLines(fullfilename);
                    string[,] round = new string[18, 3];
                    int roundnum = 0;
                    int holenum = 0;
                    foreach(string line in lines)
                    {
                        round[roundnum,holenum] = line;
                        if (holenum == 2)
                        {
                            roundnum++;
                            holenum = 0;
                        }
                        else
                        {
                            holenum++;
                        }
                    }
                    scores.Add(round);
                    filenums++;
                }
                UpdateText(CalculateHandicap());
            };
        }

        private Boolean CheckFileExists(String filename)
        {
            filename = Path.Combine(System.Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData), filename + ".txt");
            if (File.Exists(filename))
            {
                return true;
            }
            return false;
        }

        private string CalculateHandicap()
        {
            if (scores.Count == 0)
            {
                return "No Scores Found, Please Save Some Rounds First.";
            }

            float averageDifference = 0;
            int loopcount = 0;
            foreach(string[,] round in scores)
            {
                
                for (int i = 0; i < 18; i++)
                {
                    averageDifference += int.Parse(round[i,2]) -
                            int.Parse(round[i,1]);
                }
                loopcount++;
            }
            return (averageDifference / (loopcount)).ToString();
        }

        private void UpdateText(string displaytext)
        {
            TextView holetext = FindViewById<TextView>(Resource.Id.scoredifferencetext);
            string finaltext = GetString(Resource.String.differenceText)+displaytext;
            holetext.SetText(finaltext, TextView.BufferType.Normal);
        }

        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Android.Content.PM.Permission[] grantResults)
        {
            Xamarin.Essentials.Platform.OnRequestPermissionsResult(requestCode, permissions, grantResults);

            base.OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    
}