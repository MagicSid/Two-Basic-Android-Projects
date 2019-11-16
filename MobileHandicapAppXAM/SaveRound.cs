using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace MobileHandicapApp
{
    [Activity(Label = "Activity1")]
    public class SaveRound : Activity
    {
        private int currenthole;
        String[,] scores;
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.Content_SaveRound);
            currenthole = 1;
            UpdateText();
            scores = new string[18,3];
            ConfigureSaveHoleButton();
            ConfigureBackButton();
        }

        private void ConfigureSaveHoleButton()
        {
            Button button = FindViewById<Button>(Resource.Id.saveHoleButton);
            button.Click += delegate
            { 
                Spinner SISpinner = FindViewById<Spinner>(Resource.Id.siSpinner);
                Spinner ParSpinner = FindViewById<Spinner>(Resource.Id.parSpinner);
                Spinner ScoreSpinner = FindViewById<Spinner>(Resource.Id.scoreSpinner);

                scores[currenthole - 1,0] = SISpinner.SelectedItem.ToString();
                scores[currenthole - 1,1] = ParSpinner.SelectedItem.ToString();
                scores[currenthole - 1,2] = ScoreSpinner.SelectedItem.ToString();

                if (currenthole < 18)
                {
                    currenthole += 1;
                    UpdateText();
                    SISpinner.SetSelection(currenthole-1);
                    ScoreSpinner.SetSelection(2);
                }
                else
                {
                    if (CheckSI())
                    {
                        SaveRoundData();
                        Finish();
                    }
                    else
                    {
                        Toast.MakeText(this.ApplicationContext, 
                            "Please enter a different Stroke Index for each hole", 
                            ToastLength.Long).Show();
                    }
                }

            };
        }


        private void ConfigureBackButton()
        {
            Button button = FindViewById<Button>(Resource.Id.backButton);
            button.Click += delegate
            {
                if (currenthole == 1)
                {
                    Finish();
                }
                else
                {
                    currenthole--;
                    UpdateText();
                }
            };
        }

        private void SaveRoundData()
        {
            int filenum = 0;
            while (CheckFileExists("Scores"+filenum))
            {
                filenum += 1;
            }
            String textToWrite = "";
            for (int round = 0; round < 18; round++)
            {
                for(int hole = 0; hole < 3; hole++)
                {
                    textToWrite += scores[round, hole];
                    textToWrite += System.Environment.NewLine;
                }
            }
            try
            {
                string pathname = Path.Combine(System.Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData), "Scores" + filenum + ".txt");
                File.WriteAllText(pathname, textToWrite);
            } catch (Exception e)
            {
                Toast.MakeText(this.ApplicationContext, e.Message, ToastLength.Long);
            }
        }

        private Boolean CheckFileExists(String filename)
        {
            filename = Path.Combine(System.Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData), filename+".txt");
            if (File.Exists(filename))
            {
                return true;
            }
            return false;
        }

        private Boolean CheckSI()
        {
            String[] SIList = new string[18];
            for(int i = 0; i < 18; i++)
            {
                if (SIList[int.Parse(scores[i, 0])-1] != null)
                {
                    return false;
                    
                }
                else
                {
                    SIList[int.Parse(scores[i, 0])-1] = "Used";
                }
            }
            return true;
        }
        private void UpdateText()
        {
            TextView holetext = FindViewById<TextView>(Resource.Id.holeText);
            string displaytext = GetString(Resource.String.hole) + currenthole;
            holetext.SetText(displaytext, TextView.BufferType.Normal);
            
        }
    }
}